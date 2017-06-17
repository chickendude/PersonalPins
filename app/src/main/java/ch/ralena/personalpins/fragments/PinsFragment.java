package ch.ralena.personalpins.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import ch.ralena.personalpins.adapters.PinsAdapter;
import ch.ralena.personalpins.objects.Pin;
import ch.ralena.personalpins.objects.Tag;
import io.realm.Realm;

public class PinsFragment extends Fragment {
	private static final String TAG = PinsFragment.class.getSimpleName();
	Realm realm;
	List<Pin> pins;

	private PinsAdapter adapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		realm = Realm.getDefaultInstance();
		View view = inflater.inflate(R.layout.fragment_pins, container, false);
		setHasOptionsMenu(true);

		// initialize pins
		pins = realm.where(Pin.class).findAll();


		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

		adapter = new PinsAdapter(pins);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.pins, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionTakePicture:
				takePicture();
				break;
			case R.id.actionTakeVideo:
				takeVideo();
				break;
			case R.id.actionChoosePicture:
				choosePicture();
				break;
			case R.id.actionChooseVideo:
				chooseVideo();
				break;
		}
		return true;
	}

	private void choosePicture() {

	}

	private void chooseVideo() {

	}

	private void takeVideo() {
	}

	private void takePicture() {
		createPin();
	}

	private void createPin() {
		Log.d(TAG, "Creating pin #" + pins.size());
		realm.executeTransaction(r -> {
			Pin pin = r.createObject(Pin.class);
			pin.setTitle("Pin #" + pins.size());
			pin.setNote("This is a note.");
			List<Tag> tags = r.where(Tag.class).findAll();
			if (tags.size() > 0) {
				pin.getTags().addAll(tags);
			}
		});
		adapter.notifyDataSetChanged();
	}

	private void closeFAB() {

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
}
