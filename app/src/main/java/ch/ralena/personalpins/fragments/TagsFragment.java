package ch.ralena.personalpins.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ch.ralena.personalpins.R;
import ch.ralena.personalpins.adapters.TagsAdapter;
import ch.ralena.personalpins.objects.Tag;
import io.realm.Realm;

public class TagsFragment extends Fragment {
	private static final String TAG = TagsFragment.class.getSimpleName();

	private Realm realm;
	private List<Tag> tags;
	private TagsAdapter adapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tags, container, false);
		setHasOptionsMenu(true);

		tags = realm.where(Tag.class).findAll();

		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

		adapter = new TagsAdapter(tags);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
		}
		return super.onOptionsItemSelected(item);
	}

	private void addTag() {
		realm.executeTransaction(r -> {
			Log.d(TAG, "added tag, num tags = " + tags.size());
			Tag tag = r.createObject(Tag.class);
			tag.setTitle("Tag #" + tags.size());
			adapter.notifyDataSetChanged();
		});
	}
}
