package ch.ralena.personalpins.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.ralena.personalpins.MainActivity;
import ch.ralena.personalpins.R;
import ch.ralena.personalpins.objects.Pin;
import ch.ralena.personalpins.objects.Tag;
import io.realm.Realm;

import static ch.ralena.personalpins.R.id.tagTitle;

public class PinDetailFragment extends Fragment {
	private Realm realm;
	private Pin pin;
	private MainActivity mainActivity;
	private LinearLayout tagLayout;
	private TextView titleText, noteText;
	private Button deleteButton;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// remove titlebar
		mainActivity = (MainActivity) getActivity();
		mainActivity.hideActionBar();

		// get realm and pin object
		realm = Realm.getDefaultInstance();
		String id = getArguments().getString(PinsFragment.EXTRA_PIN_ID);
		pin = realm.where(Pin.class).equalTo("id", id).findFirst();

		View view = inflater.inflate(R.layout.fragment_pin_detail, container, false);
		tagLayout = (LinearLayout) view.findViewById(R.id.tagLayout);
		titleText = (TextView) view.findViewById(R.id.titleText);
		noteText = (TextView) view.findViewById(R.id.noteText);
		deleteButton = (Button) view.findViewById(R.id.deleteButton);
		deleteButton.setOnClickListener(v -> new AlertDialog.Builder(getContext())
				.setMessage("Are you sure you want to delete this pin?")
				.setPositiveButton("Yes", (dialog, which) -> {
					realm.executeTransaction(r -> r
							.where(Pin.class)
							.equalTo("id", id)
							.findFirst()
							.deleteFromRealm());
					getFragmentManager().popBackStack();
				})
				.setNegativeButton("No", (dialog, which) -> dialog.cancel())
				.create()
				.show()
		);

		titleText.setText(pin.getTitle());
		noteText.setText(pin.getNote());

		loadTags();

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mainActivity.showActionBar();
	}

	private void loadTags() {
		for (Tag tag : pin.getTags()) {

			View tagView = ((LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.view_tag_button, tagLayout, false);
			TextView title = (TextView) tagView.findViewById(tagTitle);
			title.setText(tag.getTitle());
			tagLayout.addView(tagView);

			tagView.setOnClickListener(v -> {
				// view list of pins with that tag
				//	String title1 = ((TextView) v.findViewById(tagTitle)).getText().toString();
				//	Tag tag = new Tag(title1);
				//	tags.remove(tag);
				((ViewGroup) v.getParent()).removeView(v);
			});
		}
	}

}
