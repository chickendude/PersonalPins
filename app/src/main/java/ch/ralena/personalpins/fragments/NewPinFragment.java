package ch.ralena.personalpins.fragments;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.ralena.personalpins.FullScreenImageActivity;
import ch.ralena.personalpins.FullScreenVideoActivity;
import ch.ralena.personalpins.MainActivity;
import ch.ralena.personalpins.R;
import ch.ralena.personalpins.objects.Pin;
import ch.ralena.personalpins.objects.Tag;
import io.realm.Realm;

public class NewPinFragment extends Fragment {
	private MainActivity mainActivity;

	// views
	private ActionBar toolbar;
	private LinearLayout tagLayout;
	private TextView tagTitle;
	private TextView tagNote;
	private AutoCompleteTextView tagEdit;

	private String filepath, filetype;
	private Realm realm;
	private ArrayAdapter<String> arrayAdapter;
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
		tagTitle = (TextView) view.findViewById(R.id.title);
		tagNote = (TextView) view.findViewById(R.id.note);

		// load thumbnail
		ImageView thumbnailPhoto = (ImageView) view.findViewById(R.id.thumbnailPhoto);
		VideoView thumbnailVideo = (VideoView) view.findViewById(R.id.thumbnailVideo);
		filepath = getArguments().getString(PinsFragment.EXTRA_FILEPATH);
		filetype = getArguments().getString(PinsFragment.EXTRA_FILETYPE);
		if (filepath != null && filetype != null) {
			if (filetype.equals("photo")) {
				thumbnailPhoto.setVisibility(View.VISIBLE);
				Picasso.with(view.getContext())
						.load(filepath)
						.into(thumbnailPhoto);
				thumbnailPhoto.setOnClickListener(v -> {
					Intent intent = new Intent(getActivity(), FullScreenImageActivity.class);
					intent.putExtra(FullScreenImageActivity.EXTRA_IMAGE_URI, filepath);
					startActivity(intent);
				});
			} else if (filetype.equals("video")) {
				thumbnailVideo.setVisibility(View.VISIBLE);
				thumbnailVideo.setVideoURI(Uri.parse(filepath));
				thumbnailVideo.setOnCompletionListener(MediaPlayer::start);
				thumbnailVideo.start();
				thumbnailVideo.setOnTouchListener((v, event) -> {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						Intent intent = new Intent(getActivity(), FullScreenVideoActivity.class);
						intent.putExtra(FullScreenVideoActivity.EXTRA_VIDEO_URI, filepath);
						startActivity(intent);
					}
					return true;	// super.onTouchEvent(event) ??
				});
			}
		}
		// load tagStrings
		tagEdit = (AutoCompleteTextView) view.findViewById(R.id.addTags);
		setupTagListeners();
		loadTags();

		return view;
	}

	private void addTag(String tagTitle) {
		arrayAdapter.notifyDataSetChanged();
		tags.add(new Tag(tagTitle));

		View tagView = ((LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.view_tag_button, tagLayout, false);

		TextView title = (TextView) tagView.findViewById(R.id.tagTitle);
		title.setText(tagTitle);
		tagLayout.addView(tagView);

		tagView.setOnClickListener(v -> {
			String title1 = ((TextView) v.findViewById(R.id.tagTitle)).getText().toString();
			Tag tag = new Tag(title1);
			tags.remove(tag);
			((ViewGroup) v.getParent()).removeView(v);
		});
	}

	private void loadTags() {
		// get list of tags
		tags = new ArrayList<>();
		tagStrings = new ArrayList<>();
		for (Tag tag : realm.where(Tag.class).findAll()) {
			tagStrings.add(tag.getTitle());
		}

		// set up completion adapter
		arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
		arrayAdapter.addAll(tagStrings);
		tagEdit.setAdapter(arrayAdapter);
	}

	private void setupTagListeners() {
		// set up listeners
		tagEdit.setOnClickListener(v -> tagEdit.showDropDown());
		tagEdit.setOnEditorActionListener((v, actionId, event) -> {
			if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
				return false;
			} else {
				String tagTitle = v.getText().toString();
				Tag tag = new Tag(tagTitle);
				if (!tags.contains(tag)) {
					tagEdit.setText("");
					addTag(tagTitle);
				}
				return true;
			}
		});
		tagEdit.setOnItemClickListener((parent, clickedView, position, id) -> {
			String tagTitle = parent.getItemAtPosition(position).toString();
			if (tagStrings.contains(tagTitle)) {
				tagEdit.setText("");
				addTag(tagTitle);
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.ok, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.actionConfirm) {
			if (tagTitle.getText().toString().trim().length() > 0) {
				realm.executeTransaction(r -> {
					Pin pin = r.createObject(Pin.class, UUID.randomUUID().toString());
					pin.setTitle(tagTitle.getText().toString());
					pin.setNote(tagNote.getText().toString());
					pin.setFilepath(filepath);
					pin.setType(filetype);
					pin.getTags().addAll(tags);
				});
				getFragmentManager().popBackStack();
			} else {
				Toast.makeText(mainActivity, "Please add a title", Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		toolbar.setDisplayHomeAsUpEnabled(false);
	}
}
