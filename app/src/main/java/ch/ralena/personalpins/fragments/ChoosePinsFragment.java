package ch.ralena.personalpins.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import ch.ralena.personalpins.MainActivity;
import ch.ralena.personalpins.R;
import ch.ralena.personalpins.adapters.ChoosePinsAdapter;
import ch.ralena.personalpins.objects.Pin;
import io.realm.Realm;

import static ch.ralena.personalpins.fragments.PinsFragment.EXTRA_PIN_ID;

public class ChoosePinsFragment extends Fragment {
	private static final String EXTRA_CHECKED_PINS = "checked_pins";
	private MainActivity mainActivity;

	// views
	private ActionBar toolbar;

	private Realm realm;
	private List<Pin> pins;
	private List<Pin> checkedPins;    // the list of currently checked pins

	private View rootView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		realm = Realm.getDefaultInstance();
		pins = realm.where(Pin.class).findAllSorted("title");

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

		// if we are returning from the pin detail fragment, rootview won't be null
		if (rootView == null) {
			checkedPins = new ArrayList<>();
			// load views
			rootView = inflater.inflate(R.layout.fragment_choose_pins, container, false);

			// setup recyclerview
			ChoosePinsAdapter adapter = new ChoosePinsAdapter(pins, checkedPins);
			RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
			recyclerView.setAdapter(adapter);
			recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

			adapter.asThumbnailObservable().subscribe(this::thumbnailClicked);
		}
		return rootView;
	}

	private void thumbnailClicked(ChoosePinsAdapter.PinView pinView) {
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.ok, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.actionConfirm) {

			ArrayList<String> checkedPinIds = new ArrayList<>();
			for (Pin pin : checkedPins) {
				checkedPinIds.add(pin.getId());
			}
			NewBoardFragment fragment = new NewBoardFragment();
			Bundle bundle = new Bundle();
			bundle.putStringArrayList(EXTRA_CHECKED_PINS, checkedPinIds);
			fragment.setArguments(bundle);

			getFragmentManager().beginTransaction()
					.replace(R.id.frameContainer, fragment)
					.addToBackStack(null)
					.commit();
			return true;
		}
		return false;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		toolbar.setDisplayHomeAsUpEnabled(false);
		if (realm != null) {
			realm.close();
			realm = null;
		}

	}
}
