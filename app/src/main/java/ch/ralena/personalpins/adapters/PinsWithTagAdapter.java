package ch.ralena.personalpins.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ch.ralena.personalpins.R;
import ch.ralena.personalpins.objects.Pin;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class PinsWithTagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	List<Pin> pins;

	private PublishSubject<Pin> onClickPin = PublishSubject.create();

	public PinsWithTagAdapter(List<Pin> pins) {
		this.pins = pins;
	}

	public Observable<Pin> asObservable() {
		return onClickPin;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pin, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		((ViewHolder)holder).bindView(pins.get(position));
	}

	@Override
	public int getItemCount() {
		return pins.size();
	}

	private class ViewHolder extends RecyclerView.ViewHolder {
		private TextView pinTitle;
		public ViewHolder(View itemView) {
			super(itemView);
			pinTitle = (TextView) itemView.findViewById(R.id.pinTitle);
		}

		public void bindView(Pin pin) {
			pinTitle.setText(pin.getTitle());
			itemView.setOnClickListener(v -> onClickPin.onNext(pin));
		}
	}
}
