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
import ch.ralena.personalpins.objects.Board;
import ch.ralena.personalpins.objects.Pin;
import io.realm.Realm;

import static ch.ralena.personalpins.fragments.PinsFragment.EXTRA_PIN_ID;

public class ChoosePinsFragment extends Fragment {
	public static final String EXTRA_CHECKED_PINS = "extra_checked_pins";
	public static final String EXTRA_ACTION = "extra_action";
	public static final String EXTRA_BOARD_ID = "extra_board_id";
	public static final String ACTION_UPDATE = "action_update";
	public static final String ACTION_NEW = "action_new";

	private MainActivity mainActivity;

	// views
	private ActionBar toolbar;

	private Realm realm;
	private Board board;
	private List<Pin> pins;
	private List<Pin> checkedPins;        // the list of currently checked pins

	private String action;                // what action to do when confirming


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		realm = Realm.getDefaultInstance();
		pins = realm.where(Pin.class).findAllSorted("title");
		action = getArguments().getString(EXTRA_ACTION);

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


		// if we are returning from the pin detail fragment, our data is already initialized
		if (checkedPins == null) {
			checkedPins = new ArrayList<>();
		}

		if (action.equals(ACTION_UPDATE)) {
			String id = getArguments().getString(EXTRA_BOARD_ID);
			board = realm.where(Board.class).equalTo("id", id).findFirst();
			checkedPins.addAll(board.getPins());
		}


		// load views
		View view = inflater.inflate(R.layout.fragment_choose_pins, container, false);

		// setup recyclerview
		ChoosePinsAdapter adapter = new ChoosePinsAdapter(pins, checkedPins);
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

		adapter.asThumbnailObservable().subscribe(this::thumbnailClicked);
		return view;
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
			if(action.equals(ACTION_NEW)) {
				newBoard();
			} else if (action.equals(ACTION_UPDATE)) {
				updateBoard();
			}
			return true;
		}
		return false;
	}

	private void updateBoard() {
		realm.executeTransaction(r -> {
			board.getPins().removeAll(board.getPins());
			board.getPins().addAll(checkedPins);
		});
		getFragmentManager().popBackStack();
	}

	private void newBoard() {
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
