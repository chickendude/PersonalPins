package ch.ralena.personalpins.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Explode;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ralena.personalpins.R;
import ch.ralena.personalpins.adapters.BoardAdapter;
import ch.ralena.personalpins.objects.Board;
import ch.ralena.personalpins.objects.Pin;
import io.realm.Realm;

public class BoardFragment extends Fragment {
	private static final String TAG = BoardFragment.class.getSimpleName();
	public static final String BACK_STACK_BOARD = "back_stack_board";
	public static final String EXTRA_BOARD_ID = "extra_board_id";
	private Realm realm;
	private List<Board> boards;
	private List<Board> allBoards;
	private BoardAdapter adapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		realm = Realm.getDefaultInstance();
		allBoards = realm.where(Board.class).findAllSorted("title");
		boards = new ArrayList<>(allBoards);

		// load views
		View view = inflater.inflate(R.layout.fragment_board, container, false);

		EditText searchBoard = (EditText) view.findViewById(R.id.editText);
		searchBoard.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String searchText = s.toString().toLowerCase();
				for (Board board : allBoards) {
					if (!board.getTitle().toLowerCase().contains(searchText))
						boards.remove(board);
					else if (!boards.contains(board))
						boards.add(board);
				}
				// make sure we're still in alphabetical order
				Collections.sort(boards, (o1, o2) -> o1.getTitle().compareTo(o2.getTitle()));
				adapter.notifyDataSetChanged();
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});


		adapter = new BoardAdapter(boards);
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
		if (!realm.where(Pin.class).findAll().isEmpty()) {
			Bundle bundle = new Bundle();
			bundle.putString(ChoosePinsFragment.EXTRA_ACTION, ChoosePinsFragment.ACTION_NEW);
			ChoosePinsFragment fragment = new ChoosePinsFragment();
			fragment.setArguments(bundle);
			getFragmentManager()
					.beginTransaction()
					.addToBackStack(BACK_STACK_BOARD)
					.replace(R.id.frameContainer, fragment)
					.commit();
		} else {
			Toast.makeText(getContext(), "You need to make some pins before you can create a board!", Toast.LENGTH_SHORT).show();
		}
	}


}
