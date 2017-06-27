package ch.ralena.personalpins.adapters;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import ch.ralena.personalpins.R;
import ch.ralena.personalpins.objects.Pin;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class NewBoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final String TAG = NewBoardAdapter.class.getSimpleName();
	private static final int TYPE_PIN = 0;
	private static final int TYPE_NEW = 1;

	private Pin checkedPin;

	private final PublishSubject<Pin> onPinClickSubject = PublishSubject.create();

	List<Pin> pins;

	public NewBoardAdapter(List<Pin> pins) {
		this.pins = pins;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_pins, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		((ViewHolder) holder).bindView(pins.get(position));
	}

	@Override
	public int getItemCount() {
		return pins.size();
	}

	private class ViewHolder extends RecyclerView.ViewHolder {
		ImageView thumbnailImage;
		RelativeLayout videoContainer;
		VideoView thumbnailVideo;
		TextView title;
		CheckBox checkBox;

		public ViewHolder(View itemView) {
			super(itemView);
			thumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnailImage);
			videoContainer = (RelativeLayout) itemView.findViewById(R.id.videoContainer);
			thumbnailVideo = (VideoView) itemView.findViewById(R.id.thumbnailVideo);
			title = (TextView) itemView.findViewById(R.id.pinTitle);
			checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
		}

		public void bindView(Pin pin) {
			checkBox.setChecked(false);

			new Handler().post(() -> checkBox.setChecked(pin==checkedPin));

			checkBox.setOnClickListener((buttonView) -> {
				checkedPin = pin;
				onPinClickSubject.onNext(pin);
				notifyDataSetChanged();
			});

			// load image/video
			if (pin.getFilepath() != null) {
				if (pin.getType().equals(Pin.TYPE_PICTURE)) {
					thumbnailImage.setVisibility(View.VISIBLE);
					videoContainer.setVisibility(View.INVISIBLE);
					Uri imageUri = Uri.fromFile(new File(pin.getFilepath()));
					Picasso.with(thumbnailImage.getContext())
							.load(imageUri)
							.fit()
							.centerCrop()
							.into(thumbnailImage);
				} else if (pin.getType().equals(Pin.TYPE_VIDEO)) {
					thumbnailImage.setVisibility(View.INVISIBLE);
					videoContainer.setVisibility(View.VISIBLE);
					thumbnailVideo.setVideoURI(Uri.parse(pin.getFilepath()));
					thumbnailVideo.seekTo(1);
				}
			}
			// update title
			title.setText(pin.getTitle());
		}
	}

	public Observable<Pin> asObservable() {
		return onPinClickSubject;
	}

	public class PinView {
		private Pin pin;
		private View view;

		public PinView(Pin pin, View view) {
			this.pin = pin;
			this.view = view;
		}

		public Pin getPin() {
			return pin;
		}

		public View getView() {
			return view;
		}
	}
}
