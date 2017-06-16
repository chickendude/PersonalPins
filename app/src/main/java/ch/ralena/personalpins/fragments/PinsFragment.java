package ch.ralena.personalpins.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ch.ralena.personalpins.R;
import ch.ralena.personalpins.adapters.PinsAdapter;
import ch.ralena.personalpins.objects.Pin;

public class PinsFragment extends Fragment {
	RecyclerView recyclerView;
	List<Pin> pins;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pins, container, false);

		// initialize pins
		pins = new ArrayList<>();

		recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

		PinsAdapter adapter = new PinsAdapter();
		recyclerView.setAdapter(adapter);

		return view;
	}
}
