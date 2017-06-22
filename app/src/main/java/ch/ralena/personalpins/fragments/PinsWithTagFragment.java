package ch.ralena.personalpins.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ch.ralena.personalpins.R;
import ch.ralena.personalpins.adapters.PinsWithTagAdapter;
import ch.ralena.personalpins.objects.Pin;
import ch.ralena.personalpins.objects.Tag;
import io.realm.Realm;

public class PinsWithTagFragment extends Fragment {
	private Realm realm;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pins_with_tag, container, false);

		realm = Realm.getDefaultInstance();

		String tagTitle = getArguments().getString(TagsFragment.EXTRA_TAG_TITLE);
		Tag tag = realm.where(Tag.class).equalTo("title", tagTitle).findFirst();

		List<Pin> allPins = realm.where(Pin.class).findAll();
		List<Pin> pins = new ArrayList<>();
		for (Pin pin : allPins) {
			List<Tag> pinTags = pin.getTags();
			if (pinTags.contains(tag))
				pins.add(pin);
		}

		PinsWithTagAdapter adapter = new PinsWithTagAdapter(pins);
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

		return view;
	}
}
