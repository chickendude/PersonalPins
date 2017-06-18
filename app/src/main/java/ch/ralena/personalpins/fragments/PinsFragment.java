package ch.ralena.personalpins.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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

import java.util.List;

import ch.ralena.personalpins.MainActivity;
import ch.ralena.personalpins.R;
import ch.ralena.personalpins.adapters.PinsAdapter;
import ch.ralena.personalpins.objects.Pin;
import io.realm.Realm;

public class PinsFragment extends Fragment {
	private static final String TAG = PinsFragment.class.getSimpleName();
	private static final int REQUEST_TAKE_PHOTO = 0;
	private static final int REQUEST_TAKE_VIDEO = 1;
	private static final int REQUEST_CHOOSE_PICTURE = 2;
	private static final int REQUEST_CHOOSE_VIDEO = 3;
	public static final String EXTRA_FILEPATH = "extra_filepath";

	private MainActivity mainActivity;
	Realm realm;
	List<Pin> pins;

	private PinsAdapter adapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		realm = Realm.getDefaultInstance();
		View view = inflater.inflate(R.layout.fragment_pins, container, false);
		setHasOptionsMenu(true);

		mainActivity = (MainActivity) getActivity();

		// initialize pins
		pins = realm.where(Pin.class).findAll();


		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

		adapter = new PinsAdapter(pins);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.pins, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.actionTakePicture:
				takePicture();
				break;
			case R.id.actionTakeVideo:
				takeVideo();
				break;
			case R.id.actionChoosePicture:
				choosePicture();
				break;
			case R.id.actionChooseVideo:
				chooseVideo();
				break;
		}
		return true;
	}

	private void takePicture() {
	}

	private void takeVideo() {
	}

	private void choosePicture() {
		mainActivity.requestReadExternalStoragePermission();
		Intent pickPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
		pickPhotoIntent.setType("image/*");
		startActivityForResult(pickPhotoIntent, REQUEST_CHOOSE_PICTURE);
	}

	private void chooseVideo() {
		mainActivity.requestReadExternalStoragePermission();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			String filepath = "";
			if (requestCode == REQUEST_CHOOSE_PICTURE) {
				if (data != null) {
					filepath = data.getData().toString();

					String wholeID = DocumentsContract.getDocumentId(data.getData());

					// Split at colon, use second item in the array
					String id = wholeID.split(":")[1];

					String[] column = { MediaStore.Images.Media.DATA };

					// where id is equal to
					String sel = MediaStore.Images.Media._ID + "=?";

					Cursor cursor = mainActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							column, sel, new String[]{ id }, null);

					int columnIndex = cursor.getColumnIndex(column[0]);

					if (cursor.moveToFirst()) {
						filepath = cursor.getString(columnIndex);
					}
					cursor.close();

				}
			}
			if (!filepath.equals("")) {
				createPin(filepath);
			}
		}
	}

	private void createPin(String filepath) {
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_FILEPATH, filepath);
		NewPinFragment fragment = new NewPinFragment();
		fragment.setArguments(bundle);

		getFragmentManager().beginTransaction()
				.replace(R.id.frameContainer, fragment)
				.addToBackStack(null)
				.commit();

//		realm.executeTransaction(r -> {
//			Pin pin = r.createObject(Pin.class);
//			pin.setTitle("Pin #" + pins.size());
//			pin.setNote("This is a note.");
//			List<Tag> tags = r.where(Tag.class).findAll();
//			if (tags.size() > 0) {
//				pin.getTags().addAll(tags);
//			}
//		});
//		adapter.notifyDataSetChanged();
	}

	private void closeFAB() {

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
}
