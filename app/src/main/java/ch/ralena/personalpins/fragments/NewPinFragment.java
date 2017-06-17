package ch.ralena.personalpins.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ch.ralena.personalpins.MainActivity;
import ch.ralena.personalpins.R;

public class NewPinFragment extends Fragment {
	private ActionBar toolbar;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_new_pin, container, false);
		MainActivity mainActivity = (MainActivity) getActivity();
		mainActivity.showActionBar();	// make sure action bar is fully shown
		mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		toolbar = mainActivity.getSupportActionBar();
		toolbar.setDisplayHomeAsUpEnabled(true);

		ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

		String filepath = getArguments().getString(PinsFragment.EXTRA_FILEPATH);
		Picasso.with(view.getContext())
				.load(filepath)
				.into(thumbnail);

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		toolbar.setDisplayHomeAsUpEnabled(false);
	}
}
