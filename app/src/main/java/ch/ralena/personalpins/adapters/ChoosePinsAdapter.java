package ch.ralena.personalpins.adapters;

import android.net.Uri;
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

import java.util.List;

import ch.ralena.personalpins.R;
import ch.ralena.personalpins.objects.Pin;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class ChoosePinsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final String TAG = ChoosePinsAdapter.class.getSimpleName();
	private static final int TYPE_PIN = 0;
	private static final int TYPE_NEW = 1;


	private final PublishSubject<PinView> onClickSubject = PublishSubject.create();

	List<Pin> pins;
	List<Pin> checkedPins;

	public ChoosePinsAdapter(List<Pin> pins, List<Pin> checkedPins) {
		this.pins = pins;
		this.checkedPins = checkedPins;
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
			checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
				if (isChecked) {
					checkedPins.add(pin);
				} else {
					checkedPins.remove(pin);
				}
			});
			checkBox.setChecked(checkedPins.contains(pin));
			itemView.setOnClickListener(v -> onClickSubject.onNext(new PinView(pin, thumbnailImage)));
			// load image/video
			if (pin.getFilepath() != null) {
				if (pin.getType().equals("photo")) {
					thumbnailImage.setVisibility(View.VISIBLE);
					videoContainer.setVisibility(View.INVISIBLE);
					Picasso.with(thumbnailImage.getContext())
							.load(pin.getFilepath())
							.fit()
							.centerCrop()
							.into(thumbnailImage);
				} else if (pin.getType().equals("video")) {
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

	public Observable<PinView> asThumbnailObservable() {
		return onClickSubject;
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
