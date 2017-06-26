package ch.ralena.personalpins.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.ralena.personalpins.MainActivity;
import ch.ralena.personalpins.R;
import ch.ralena.personalpins.adapters.NewBoardAdapter;
import ch.ralena.personalpins.objects.Board;
import ch.ralena.personalpins.objects.Pin;
import io.realm.Realm;

public class NewBoardFragment extends Fragment {
	private MainActivity mainActivity;

	// views
	private TextView boardTitle;

	private Realm realm;
	private List<Pin> pins;
	private String coverFilepath;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		realm = Realm.getDefaultInstance();

		// set up toolbar
		mainActivity = (MainActivity) getActivity();
		mainActivity.showActionBar();    // make sure action bar is fully shown
		mainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		setHasOptionsMenu(true);

		List<String> pinIds = getArguments().getStringArrayList(ChoosePinsFragment.EXTRA_CHECKED_PINS);
		pins = new ArrayList<>();
		for (String id : pinIds) {
			Pin pin = realm.where(Pin.class).equalTo("id", id).findFirst();
			pins.add(pin);
		}

		coverFilepath = "";

		// load views
		View view = inflater.inflate(R.layout.fragment_new_board, container, false);
		boardTitle = (TextView) view.findViewById(R.id.boardTitle);

		// set up recycler view
		NewBoardAdapter adapter = new NewBoardAdapter(pins);
		adapter.asObservable().subscribe(pin -> coverFilepath = pin.getFilepath());
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.ok, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.actionConfirm) {
			if (boardTitle.getText().toString().trim().length() > 0) {
				realm.executeTransaction(r -> {
					Board board = r.createObject(Board.class, UUID.randomUUID().toString());
					board.setTitle(boardTitle.getText().toString());
					board.getPins().addAll(pins);
					board.setCoverFilepath(coverFilepath);
				});
				getFragmentManager().popBackStack();
			} else {
				Toast.makeText(mainActivity, "Please add a title", Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		if (realm != null) {
			realm.close();
			realm = null;
		}

	}
}
