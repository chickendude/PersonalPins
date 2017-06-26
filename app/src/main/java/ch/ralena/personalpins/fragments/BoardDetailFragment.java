package ch.ralena.personalpins.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ralena.personalpins.MainActivity;
import ch.ralena.personalpins.R;
import ch.ralena.personalpins.adapters.PinsAdapter;
import ch.ralena.personalpins.objects.Board;
import ch.ralena.personalpins.objects.Pin;
import io.realm.Realm;

public class BoardDetailFragment extends Fragment {
	private static final String TAG = BoardDetailFragment.class.getSimpleName();
	private static final int REQUEST_TAKE_PHOTO = 0;
	private static final int REQUEST_TAKE_VIDEO = 1;
	private static final int REQUEST_CHOOSE_PICTURE = 2;
	private static final int REQUEST_CHOOSE_VIDEO = 3;
	public static final String EXTRA_FILEPATH = "extra_filepath";
	public static final String EXTRA_FILETYPE = "extra_filetype";
	public static final String EXTRA_PIN_ID = "extra_pin_id";

	private MainActivity mainActivity;
	private Realm realm;
	private Board board;
	private List<Pin> allPins;
	private List<Pin> pins;
	private Uri mediaUri;
	private String mediaPath;
	private EditText searchPins;
	private ActionBar toolbar;

	private PinsAdapter adapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		realm = Realm.getDefaultInstance();
		View view = inflater.inflate(R.layout.fragment_pins, container, false);
		setHasOptionsMenu(true);

		mainActivity = (MainActivity) getActivity();
		toolbar = mainActivity.getSupportActionBar();
		if (toolbar != null) {
			toolbar.setDisplayHomeAsUpEnabled(true);
			toolbar.setHideOnContentScrollEnabled(false);
		}

		// initialize pins
		String id = getArguments().getString(BoardFragment.EXTRA_BOARD_ID);
		board = realm.where(Board.class).equalTo("id", id).findFirst();
		toolbar.setTitle(board.getTitle());
		allPins = board.getPins();
		pins = new ArrayList<>(allPins);

		searchPins = (EditText) view.findViewById(R.id.editText);
		searchPins.setHint("Search Pins in '" + board.getTitle() + "'");
		searchPins.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String searchText = s.toString().toLowerCase();
				pins.removeIf(tag -> !tag.getTitle().toLowerCase().contains(searchText));
				for (Pin pin : allPins) {
					if (pin.getTitle().toLowerCase().contains(searchText) && !pins.contains(pin)) {
						pins.add(pin);
					}
				}
				// make sure we're still in alphabetical order
				Collections.sort(pins, (o1, o2) -> o1.getTitle().compareTo(o2.getTitle()));
				adapter.notifyDataSetChanged();
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
		adapter = new PinsAdapter(pins, true, "Add/Remove");
		adapter.asPinObservable().subscribe(this::loadPinDetail);
		adapter.asNewObservable().subscribe(this::addPin);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

		return view;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		}
		return true;
	}

	private void addPin(View view) {
		ChoosePinsFragment fragment = new ChoosePinsFragment();
		// create bundle
		Bundle bundle = new Bundle();
		bundle.putString(ChoosePinsFragment.EXTRA_ACTION, ChoosePinsFragment.ACTION_UPDATE);
		bundle.putString(ChoosePinsFragment.EXTRA_BOARD_ID, board.getId());
		fragment.setArguments(bundle);

		getFragmentManager()
				.beginTransaction()
				.addToBackStack(null)
				.replace(R.id.frameContainer, fragment)
				.commit();
	}

	private void loadPinDetail(PinsAdapter.PinView pinView) {
		View view = pinView.getView();
		Pin pin = pinView.getPin();
		view.setTransitionName(getString(R.string.image_transition));

		// create fragment
		PinDetailFragment pinDetailFragment = new PinDetailFragment();

		// set up transitions
		TransitionSet transitionSet = new TransitionSet();
		transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER)
				.addTransition(new ChangeBounds())
				.addTransition(new ChangeTransform())
				.addTransition(new ChangeImageTransform());

		// attach transitions to fragments
		pinDetailFragment.setSharedElementEnterTransition(transitionSet);
		pinDetailFragment.setSharedElementReturnTransition(new Fade());
		pinDetailFragment.setEnterTransition(new Explode());
		pinDetailFragment.setExitTransition(new Fade());
		setReenterTransition(new Explode());

		// create bundle and load fragment
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_PIN_ID, pin.getId());
		pinDetailFragment.setArguments(bundle);
		getFragmentManager()
				.beginTransaction()
				.addSharedElement(view, ViewCompat.getTransitionName(view))
				.replace(R.id.frameContainer, pinDetailFragment)
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		realm = Realm.getDefaultInstance();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		toolbar.setTitle("Personal Pins");
		if (realm != null) {
			realm.close();
			realm = null;
		}
	}
}
