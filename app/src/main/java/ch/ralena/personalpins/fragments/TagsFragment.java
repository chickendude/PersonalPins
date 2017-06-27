package ch.ralena.personalpins.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ralena.personalpins.R;
import ch.ralena.personalpins.adapters.TagsAdapter;
import ch.ralena.personalpins.objects.Tag;
import io.realm.Realm;

public class TagsFragment extends Fragment {
	private static final String TAG = TagsFragment.class.getSimpleName();
	public static final String EXTRA_TAG_TITLE = "extra_tag_title";
	public static final String BACKSTACK_PINSWITHTAG = "backstack_pinswithtag";

	private Realm realm;
	private List<Tag> tags;
	private List<Tag> allTags;
	private TagsAdapter adapter;
	private EditText searchTags;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tags, container, false);
		setHasOptionsMenu(true);

		searchTags = (EditText) view.findViewById(R.id.editText);
		searchTags.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String searchText = s.toString().toLowerCase();
				for (Tag tag : allTags) {
					if(!tag.getTitle().toLowerCase().contains(searchText))
						tags.remove(tag);
					else if (!tags.contains(tag))
						tags.add(tag);
				}
				// make sure we're still in alphabetical order
				Collections.sort(tags, (o1, o2) -> o1.getTitle().compareTo(o2.getTitle()));
				adapter.notifyDataSetChanged();
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		allTags = realm.where(Tag.class).findAllSorted("title");
		tags = new ArrayList<>(allTags);

		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

		adapter = new TagsAdapter(tags);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		adapter.asObservable().subscribe(this::loadPinWithTag);

		return view;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		realm = Realm.getDefaultInstance();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (realm != null) {
			realm.close();
			realm = null;
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.new_tag, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionAddTag:
				addTag();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void loadPinWithTag(Tag tag) {
		PinsWithTagFragment fragment = new PinsWithTagFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_TAG_TITLE, tag.getTitle());
		fragment.setArguments(bundle);

		getFragmentManager().beginTransaction()
				.addToBackStack(BACKSTACK_PINSWITHTAG)
				.replace(R.id.frameContainer, fragment)
				.commit();
	}

	private void addTag() {
		EditText tagNameEdit = new EditText(getContext());
		tagNameEdit.setHint("Tag Name");
		new AlertDialog.Builder(getContext())
				.setTitle("Add New Tag")
				.setView(tagNameEdit)
				.setPositiveButton("Add", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						realm.executeTransaction(r -> {
							boolean tagExists = false;
							for (Tag tag : tags) {
								tagExists |= tag.getTitle().equals(tagNameEdit.getText().toString());
							}
							if (!tagExists) {
								r.createObject(Tag.class, tagNameEdit.getText().toString());
								adapter.notifyDataSetChanged();
							}
						});
					}
				})
				.create()
				.show();
	}
}
