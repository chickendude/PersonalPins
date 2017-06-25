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

import java.util.ArrayList;
import java.util.List;

import ch.ralena.personalpins.R;
import ch.ralena.personalpins.adapters.BoardAdapter;
import ch.ralena.personalpins.objects.Board;
import io.realm.Realm;

public class BoardFragment extends Fragment {
	private static final String TAG = BoardFragment.class.getSimpleName();
	private Realm realm;
	private List<Board> boards;
	private List<Board> allBoards;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		realm = Realm.getDefaultInstance();
		allBoards = realm.where(Board.class).findAllSorted("title");
		boards = new ArrayList<>(allBoards);

		// load views
		View view = inflater.inflate(R.layout.fragment_board, container, false);


		BoardAdapter adapter = new BoardAdapter(boards);
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

		adapter.asNewObservable().subscribe(this::createBoard);

		return view;
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.new_board, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionNewBoard:
				createBoard(null);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void createBoard(View v) {
		ChoosePinsFragment fragment = new ChoosePinsFragment();
		getFragmentManager()
				.beginTransaction()
				.addToBackStack(null)
				.replace(R.id.frameContainer, fragment)
				.commit();
	}


}
