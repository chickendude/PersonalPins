package ch.ralena.personalpins.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ch.ralena.personalpins.R;
import ch.ralena.personalpins.objects.Pin;

public class PinsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int TYPE_PIN = 0;
	private static final int TYPE_NEW = 1;


	private static final String TAG = PinsAdapter.class.getSimpleName();
	List<Pin> pins;

	public PinsAdapter(List<Pin> pins) {
		this.pins = pins;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == TYPE_NEW) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new, parent, false);
			return new ViewHolderNew(view);
		} else {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pin, parent, false);
			return new ViewHolder(view);
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if(position < pins.size())
			((ViewHolder) holder).bindView(pins.get(position));
	}

	@Override
	public int getItemCount() {
		return pins.size() + 1;
	}

	@Override
	public int getItemViewType(int position) {
		return position == pins.size() ? TYPE_NEW : TYPE_PIN;
	}

	private class ViewHolder extends RecyclerView.ViewHolder {
		TextView title;

		public ViewHolder(View itemView) {
			super(itemView);
			title = (TextView) itemView.findViewById(R.id.pinTitle);
		}

		public void bindView(Pin pin) {
			Log.d("TAG", pin.getTitle());
			title.setText(pin.getTitle());
		}
	}

	private class ViewHolderNew extends RecyclerView.ViewHolder {
		public ViewHolderNew(View itemView) {
			super(itemView);
		}
	}
}
