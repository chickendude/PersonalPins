package ch.ralena.personalpins.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Fade;
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
	public static final String BACK_STACK_BOARD = "back_stack_board";
	public static final String EXTRA_BOARD_ID = "extra_board_id";
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

		adapter.asBoardObservable().subscribe(this::viewBoard);
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

	private void viewBoard(Board board) {
		String id = board.getId();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_BOARD_ID, id);

		BoardDetailFragment fragment = new BoardDetailFragment();
		fragment.setArguments(bundle);

		fragment.setEnterTransition(new Fade());
		setReenterTransition(new Explode());
		setExitTransition(new Explode());

		getFragmentManager().beginTransaction()
				.replace(R.id.frameContainer, fragment)
				.addToBackStack(null)
				.commit();
	}

	private void createBoard(View v) {
		Bundle bundle = new Bundle();
		bundle.putString(ChoosePinsFragment.EXTRA_ACTION, ChoosePinsFragment.ACTION_NEW);
		ChoosePinsFragment fragment = new ChoosePinsFragment();
		fragment.setArguments(bundle);
		getFragmentManager()
				.beginTransaction()
				.addToBackStack(BACK_STACK_BOARD)
				.replace(R.id.frameContainer, fragment)
				.commit();
	}


}
