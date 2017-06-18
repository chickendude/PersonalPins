package ch.ralena.personalpins.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ch.ralena.personalpins.MainActivity;
import ch.ralena.personalpins.R;
import ch.ralena.personalpins.objects.Tag;
import io.realm.Realm;

public class NewPinFragment extends Fragment {
	private MainActivity mainActivity;
	private ActionBar toolbar;
	private LinearLayout tagLayout;
	private AutoCompleteTextView tagEdit;
	private ArrayAdapter<String> arrayAdapter;
	private Realm realm;
	private List<String> tagStrings;
	private List<Tag> tags;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		realm = Realm.getDefaultInstance();

		// set up toolbar
		mainActivity = (MainActivity) getActivity();
		mainActivity.showActionBar();    // make sure action bar is fully shown
		mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		toolbar = mainActivity.getSupportActionBar();
		if (toolbar != null) {
			toolbar.setDisplayHomeAsUpEnabled(true);
			toolbar.setHideOnContentScrollEnabled(false);
		}
		setHasOptionsMenu(true);

		// load views
		View view = inflater.inflate(R.layout.fragment_new_pin, container, false);
		tagLayout = (LinearLayout) view.findViewById(R.id.tagLayout);

		// load thumbnail
		ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
		String filepath = getArguments().getString(PinsFragment.EXTRA_FILEPATH);
		Picasso.with(view.getContext())
				.load(filepath)
				.into(thumbnail);

		// load tagStrings
		tagEdit = (AutoCompleteTextView) view.findViewById(R.id.addTags);
		tagEdit.setOnClickListener(v -> tagEdit.showDropDown());
		tagEdit.setOnItemClickListener((parent, clickedView, position, id) -> {
			String tagTitle = parent.getItemAtPosition(position).toString();
//			Tag tag = new Tag(tagTitle);
			if (tagStrings.contains(tagTitle)) {
				tagEdit.setText("");
				addTag(tagTitle);
			}
		});
		loadTags();

		return view;
	}

	private void addTag(String tagTitle) {
		tagStrings.remove(tagTitle);
		arrayAdapter.notifyDataSetChanged();
		tags.add(new Tag(tagTitle));

		View tagView = ((LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.view_tag_button, tagLayout, false);

		TextView title = (TextView) tagView.findViewById(R.id.tagTitle);
		title.setText(tagTitle);
		tagLayout.addView(tagView);
	}

	private void loadTags() {
		tags = new ArrayList<>();
		tagStrings = new ArrayList<>();
		for (Tag tag : realm.where(Tag.class).findAll()) {
			tagStrings.add(tag.getTitle());
		}
		arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
		arrayAdapter.addAll(tagStrings);
		tagEdit.setAdapter(arrayAdapter);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.ok, menu);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		toolbar.setDisplayHomeAsUpEnabled(false);
	}
}
