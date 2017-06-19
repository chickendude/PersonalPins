package ch.ralena.personalpins.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ch.ralena.personalpins.MainActivity;
import ch.ralena.personalpins.R;
import ch.ralena.personalpins.adapters.PinsAdapter;
import ch.ralena.personalpins.objects.Pin;
import io.realm.Realm;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class PinsFragment extends Fragment {
	private static final String TAG = PinsFragment.class.getSimpleName();
	private static final int REQUEST_TAKE_PHOTO = 0;
	private static final int REQUEST_TAKE_VIDEO = 1;
	private static final int REQUEST_CHOOSE_PICTURE = 2;
	private static final int REQUEST_CHOOSE_VIDEO = 3;
	public static final String EXTRA_FILEPATH = "extra_filepath";
	public static final String EXTRA_FILETYPE = "extra_filetype";
	public static final String EXTRA_PIN_ID = "extra_pin_id";

	private MainActivity mainActivity;
	private Realm realm;
	private List<Pin> pins;
	private Uri mediaUri;

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
		adapter.asObservable().subscribe(this::loadPinDetail);
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			String filepath = "";
			String filetype = "";
			if (requestCode == REQUEST_CHOOSE_PICTURE || requestCode == REQUEST_TAKE_PHOTO) {
				filetype = "photo";
				if (data != null) {
					filepath = data.getData().toString();
				} else {
					filepath = mediaUri.toString();
				}
			} else if (requestCode == REQUEST_TAKE_VIDEO || requestCode == REQUEST_CHOOSE_VIDEO) {
				filetype = "video";
				if (data != null) {
					filepath = data.getData().toString();
				} else {
					filepath = mediaUri.toString();
				}
			}
			if (!filepath.equals("")) {
				createPin(filepath, filetype);
			}
		} else if (resultCode != Activity.RESULT_CANCELED) {
			Toast.makeText(mainActivity, "Sorry, there was an error!", Toast.LENGTH_SHORT).show();
		}
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
		// find a place to save the image
		mediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		if (mediaUri == null) {
			Toast.makeText(mainActivity,
					"There was a problem accessing your device's external storage.",
					Toast.LENGTH_LONG)
					.show();
		} else {
			Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
			startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
		}
	}

	private void takeVideo() {
		mediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
		if (mediaUri == null) {
			Toast.makeText(mainActivity,
					"There was a problem accessing your device's external storage.",
					Toast.LENGTH_LONG)
					.show();
		} else {
			Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
			takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
			startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
		}
	}

	private void choosePicture() {
		Intent choosePhotoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		choosePhotoIntent.setType("image/*");
		startActivityForResult(choosePhotoIntent, REQUEST_CHOOSE_PICTURE);
	}

	private void chooseVideo() {
		Intent chooseVideoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		chooseVideoIntent.setType("video/*");
		startActivityForResult(chooseVideoIntent, REQUEST_CHOOSE_VIDEO);
	}

	private void createPin(String filepath, String filetype) {
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_FILEPATH, filepath);
		bundle.putString(EXTRA_FILETYPE, filetype);
		NewPinFragment fragment = new NewPinFragment();
		fragment.setArguments(bundle);

		getFragmentManager().beginTransaction()
				.replace(R.id.frameContainer, fragment)
				.addToBackStack(null)
				.commit();
	}

	private Uri getOutputMediaFileUri(int mediaType) {
		// check for external storage
		if (isExternalStorageAvailable()) {
			// get the URI

			// 1. Get the external storage directory
			File mediaStorageDir = mainActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

			// 2. Create unique filename
			String fileName = "";
			String fileType = "";
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

			if (mediaType == MEDIA_TYPE_IMAGE) {
				fileName = "IMG_" + timeStamp;
				fileType = ".jpg";
			} else if (mediaType == MEDIA_TYPE_VIDEO) {
				fileName = "IMG_" + timeStamp;
				fileType = ".mp4";
			} else {
				return null;
			}

			// 3. Create file
			File mediaFile = null;
			try {
				mediaFile = File.createTempFile(fileName, fileType, mediaStorageDir);
				// 4. Return file's URI
				Log.i(TAG, "File: " + Uri.fromFile(mediaFile));
				Uri photoURI = FileProvider.getUriForFile(mainActivity, mainActivity.getApplicationContext().getPackageName() + ".provider", mediaFile);
				return photoURI;
			} catch (IOException e) {
				Log.e(TAG, "Error creating file:" + mediaStorageDir.getAbsolutePath() + fileName + fileType);
			}

		}

		// something went wrong
		return null;
	}

	private boolean isExternalStorageAvailable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else {
			return false;
		}
	}

	private void loadPinDetail(Pin pin) {
		PinDetailFragment pinDetailFragment = new PinDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_PIN_ID, pin.getId());
		pinDetailFragment.setArguments(bundle);
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.frameContainer, pinDetailFragment)
				.addToBackStack(null)
				.commit();
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
