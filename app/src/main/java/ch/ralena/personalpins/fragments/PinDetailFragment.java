package ch.ralena.personalpins.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.ralena.personalpins.R;
import ch.ralena.personalpins.objects.Pin;
import io.realm.Realm;

public class PinDetailFragment extends Fragment {
	private Realm realm;
	private Pin pin;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		realm = Realm.getDefaultInstance();
		String id = getArguments().getString(PinsFragment.EXTRA_PIN_ID);
		pin = realm.where(Pin.class).equalTo("id", id).findFirst();

		View view = inflater.inflate(R.layout.fragment_pin_detail, container, false);

		getActivity().setTitle(pin.getTitle());

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().setTitle("Personal Pins");
	}
}
