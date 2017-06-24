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
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ch.ralena.personalpins.MainActivity;
import ch.ralena.personalpins.R;
import ch.ralena.personalpins.adapters.PinsAdapter;
import ch.ralena.personalpins.objects.Pin;
import ch.ralena.personalpins.objects.Tag;
import io.realm.Realm;

import static ch.ralena.personalpins.fragments.PinsFragment.EXTRA_PIN_ID;

public class PinsWithTagFragment extends Fragment {
	private Realm realm;
	private MainActivity mainActivity;
	private ActionBar toolbar;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// get tag title
		realm = Realm.getDefaultInstance();
		String tagTitle = getArguments().getString(TagsFragment.EXTRA_TAG_TITLE);

		mainActivity = (MainActivity) getActivity();
		toolbar = mainActivity.getSupportActionBar();
		if (toolbar != null) {
			toolbar.setDisplayHomeAsUpEnabled(true);
			toolbar.setTitle("Pins tagged with '" + tagTitle + "'");
		}

		View view = inflater.inflate(R.layout.fragment_pins_with_tag, container, false);

		Tag tag = realm.where(Tag.class).equalTo("title", tagTitle).findFirst();

		List<Pin> allPins = realm.where(Pin.class).findAll();
		List<Pin> pins = new ArrayList<>();
		for (Pin pin : allPins) {
			List<Tag> pinTags = pin.getTags();
			if (pinTags.contains(tag))
				pins.add(pin);
		}

		PinsAdapter adapter = new PinsAdapter(pins, false);
		adapter.asPinObservable().subscribe(this::loadPinDetail);
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

		return view;
	}

	@Override
	public void onDestroyView() {
		toolbar.setTitle("Personal Pins");
		toolbar.setDisplayHomeAsUpEnabled(false);
		if (realm != null) {
			realm.close();
			realm = null;
		}
		super.onDestroyView();
	}

	private void loadPinDetail(PinsAdapter.PinView pinView) {
		View view = pinView.getView();
		Pin pin = pinView.getPin();
		view.setTransitionName(getString(R.string.image_transition));

		PinDetailFragment pinDetailFragment = new PinDetailFragment();

		// transitions
		TransitionSet transitionSet = new TransitionSet();
		transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER)
				.addTransition(new ChangeBounds())
				.addTransition(new ChangeTransform())
				.addTransition(new ChangeImageTransform());

		pinDetailFragment.setSharedElementEnterTransition(transitionSet);
		pinDetailFragment.setSharedElementReturnTransition(new Fade());
		pinDetailFragment.setEnterTransition(new Explode());
		pinDetailFragment.setExitTransition(new Fade());
		setReenterTransition(new Explode());

		// load bundle/fragment
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

}
