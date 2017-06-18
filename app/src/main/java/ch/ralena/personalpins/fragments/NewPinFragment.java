package ch.ralena.personalpins.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
			tagEdit.setText("");
			String tagTitle = parent.getItemAtPosition(position).toString();
			addTag(tagTitle);
		});
		loadTags();

		return view;
	}

	private void addTag(String tagTitle) {
		tagStrings.remove(tagTitle);
		arrayAdapter.notifyDataSetChanged();
		tags.add(new Tag(tagTitle));
		CheckedTextView button = new CheckedTextView(mainActivity);
		button.setText(tagTitle);
		button.setTextColor(ContextCompat.getColor(mainActivity, R.color.colorPrimaryDark));
		button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		button.setBackground(ContextCompat.getDrawable(mainActivity, R.drawable.bg_tag));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		int marginH = (int) TypedValue.applyDimension(
				1,
				TypedValue.COMPLEX_UNIT_DIP,
				getResources().getDisplayMetrics());
		int marginV = (int) TypedValue.applyDimension(
				2,
				TypedValue.COMPLEX_UNIT_DIP,
				getResources().getDisplayMetrics());
		lp.setMargins(marginH, marginV, marginH, marginV);
		button.setLayoutParams(lp);
		button.setTag(tags.size());
		tagLayout.addView(button);
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
