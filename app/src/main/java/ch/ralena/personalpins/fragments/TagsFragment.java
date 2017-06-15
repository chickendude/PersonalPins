package ch.ralena.personalpins.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.ralena.personalpins.R;
import ch.ralena.personalpins.adapters.PinsAdapter;

public class TagsFragment extends Fragment {
	RecyclerView recyclerView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tags, container, false);

		recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

		PinsAdapter adapter = new PinsAdapter();
		recyclerView.setAdapter(adapter);

		return view;
	}
}
