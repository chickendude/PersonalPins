package ch.ralena.personalpins.adapters;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import ch.ralena.personalpins.R;
import ch.ralena.personalpins.objects.Board;
import ch.ralena.personalpins.objects.Pin;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class BoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int TYPE_BOARD = 0;
	private static final int TYPE_NEW = 1;
	List<Board> boards;

	PublishSubject<Board> onClickBoardSubject = PublishSubject.create();
	PublishSubject<View> onClickNewSubject = PublishSubject.create();

	public BoardAdapter(List<Board> boards) {
		this.boards = boards;
	}

	public Observable<Board> asBoardObservable() {
		return onClickBoardSubject;
	}

	public Observable<View> asNewObservable() {
		return onClickNewSubject;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == TYPE_BOARD) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board, parent, false);
			return new ViewHolder(view);
		} else {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new, parent, false);
			return new ViewHolderNew(view);
		}
	}

	@Override
	public int getItemViewType(int position) {
		return position < boards.size() ? TYPE_BOARD : TYPE_NEW;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if(position < boards.size()) {
			((ViewHolder) holder).bindView(boards.get(position));
		}
	}

	@Override
	public int getItemCount() {
		return boards.size() + 1;
	}

	private class ViewHolder extends RecyclerView.ViewHolder {
		private ImageView coverImage;
		private TextView boardTitle;

		public ViewHolder(View itemView) {
			super(itemView);
			coverImage = (ImageView) itemView.findViewById(R.id.coverImage);
			boardTitle = (TextView) itemView.findViewById(R.id.boardTitle);
		}

		public void bindView(Board board) {
			itemView.setOnClickListener(v -> onClickBoardSubject.onNext(board));
			Pin pin = board.getCoverPin();
			if (pin.getType().equals(Pin.TYPE_PICTURE)) {
				Uri coverUri = Uri.fromFile(new File(pin.getFilepath()));
				Picasso.with(coverImage.getContext())
						.load(coverUri)
						.fit()
						.centerCrop()
						.into(coverImage);
			} else {
				Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(board.getCoverPin().getFilepath(), MediaStore.Video.Thumbnails.MICRO_KIND);
				coverImage.setImageBitmap(thumbnail);
			}
			boardTitle.setText(board.getTitle());
		}
	}

	private class ViewHolderNew extends RecyclerView.ViewHolder {
		public ViewHolderNew(View view) {
			super(view);
			view.setOnClickListener(v -> onClickNewSubject.onNext(v));
		}
	}
}