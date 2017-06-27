package ch.ralena.personalpins.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ch.ralena.personalpins.MainActivity;
import ch.ralena.personalpins.R;
import ch.ralena.personalpins.adapters.PinsAdapter;
import ch.ralena.personalpins.objects.Pin;
import ch.ralena.personalpins.objects.Tag;
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
	private List<Pin> allPins;
	private List<Pin> pins;
	private Uri mediaUri;
	private String mediaPath;
	private EditText searchPins;
	private HashMap<MenuItem, Pin> itemMap;

	private PinsAdapter adapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		realm = Realm.getDefaultInstance();
		View view = inflater.inflate(R.layout.fragment_pins, container, false);
		setHasOptionsMenu(true);

		searchPins = (EditText) view.findViewById(R.id.editText);
		searchPins.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String searchText = s.toString().toLowerCase();
				for (Pin pin : allPins) {
					boolean inTag = false;
					for (Tag tag : pin.getTags()) {
						inTag = inTag || tag.getTitle().toLowerCase().contains(searchText);
					}
					if (!inTag)
						pins.remove(pin);
					else if (!pins.contains(pin))
						pins.add(pin);
				}
				for (Pin pin : allPins) {
					if (pin.getTitle().toLowerCase().contains(searchText) && !pins.contains(pin)) {
						pins.add(pin);
					}
				}
				// make sure we're still in alphabetical order
				Collections.sort(pins, (o1, o2) -> o1.getTitle().compareTo(o2.getTitle()));
				adapter.notifyDataSetChanged();
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});


		mainActivity = (MainActivity) getActivity();

		// initialize pins
		allPins = realm.where(Pin.class).findAllSorted("title");
		pins = new ArrayList<>(allPins);

		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
		adapter = new PinsAdapter(pins, true, "New Pin");
		adapter.asPinObservable().subscribe(this::loadPinDetail);
		adapter.asPinLongClickObservable().subscribe(this::pinMenu);
		adapter.asNewObservable().subscribe(this::newPinMenu);
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
				filetype = Pin.TYPE_PICTURE;
				if (requestCode == REQUEST_CHOOSE_PICTURE)
					filepath = getRealPathFromURI(getContext(), data.getData(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				else
					filepath = mediaPath;
			} else if (requestCode == REQUEST_TAKE_VIDEO || requestCode == REQUEST_CHOOSE_VIDEO) {
				filetype = Pin.TYPE_VIDEO;
				if (requestCode == REQUEST_CHOOSE_VIDEO)
					filepath = getRealPathFromURI(getContext(), data.getData(), MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
				else
					filepath = mediaPath;
			}
			if (!filepath.equals("")) {
				createPin(filepath, filetype);
			}
		} else if (resultCode != Activity.RESULT_CANCELED) {
			Toast.makeText(mainActivity, "Sorry, there was an error!", Toast.LENGTH_SHORT).show();
		}
	}

	public static String getRealPathFromURI(Context context, Uri uri, Uri uriType) {
		String filePath = "";
		String wholeID = DocumentsContract.getDocumentId(uri);

		// Split at colon, use second item in the array
		String id = wholeID.split(":")[1];

		String[] column = {MediaStore.Images.Media.DATA};

		// where id is equal to
		String sel = MediaStore.Images.Media._ID + "=?";

		Cursor cursor = context.getContentResolver().query(uriType,
				column, sel, new String[]{id}, null);


		int columnIndex = cursor.getColumnIndex(column[0]);

		if (cursor.moveToFirst()) {
			filePath = cursor.getString(columnIndex);
		}
		cursor.close();
		return filePath;

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.pins, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Pin pin;
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
			case R.id.actionShare:
				pin = itemMap.get(item);
				sharePin(pin);
				break;
			case R.id.actionDelete:
				pin = itemMap.get(item);
				deletePin(pin);
				break;
		}
		return true;
	}

	private void sharePin(Pin pin) {
		File file = new File(pin.getFilepath());
		Uri uri = Uri.fromFile(file);

		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		if (pin.getType().equals(Pin.TYPE_PICTURE)) {
			shareIntent.setType("image/jpeg");
		} else {
			shareIntent.setType("video/*");
		}
		startActivity(Intent.createChooser(shareIntent, "Share pin to..."));
	}

	private void deletePin(Pin pin) {
		realm.executeTransaction(r -> r
				.where(Pin.class)
				.equalTo("id", pin.getId())
				.findFirst()
				.deleteFromRealm());
		adapter.notifyDataSetChanged();
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
		Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
		choosePhotoIntent.setType("image/*");
		startActivityForResult(choosePhotoIntent, REQUEST_CHOOSE_PICTURE);
	}

	private void chooseVideo() {
		Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
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
				mediaPath = mediaFile.getPath();
				Log.i(TAG, mediaPath);
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

	private void loadPinDetail(PinsAdapter.PinView pinView) {
		View view = pinView.getView();
		Pin pin = pinView.getPin();
		view.setTransitionName(getString(R.string.image_transition));

		// create fragment
		PinDetailFragment pinDetailFragment = new PinDetailFragment();

		// set up transitions
		TransitionSet transitionSet = new TransitionSet();
		transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER)
				.addTransition(new ChangeBounds())
				.addTransition(new ChangeTransform())
				.addTransition(new ChangeImageTransform());

		// attach transitions to fragments
		pinDetailFragment.setSharedElementEnterTransition(transitionSet);
		pinDetailFragment.setSharedElementReturnTransition(new Fade());
		pinDetailFragment.setEnterTransition(new Explode());
		pinDetailFragment.setExitTransition(new Fade());
		setReenterTransition(new Explode());

		// create bundle and load fragment
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_PIN_ID, pin.getId());
		pinDetailFragment.setArguments(bundle);
		getFragmentManager()
				.beginTransaction()
				.addSharedElement(view, ViewCompat.getTransitionName(view))
				.replace(R.id.frameContainer, pinDetailFragment)
				.addToBackStack(null)
				.commit();
	}

	private void pinMenu(PinsAdapter.PinView pinView) {
		View view = pinView.getView();
		itemMap = new HashMap<>();
		PopupMenu menu = new PopupMenu(mainActivity, view);
		menu.getMenuInflater().inflate(R.menu.pin_actions, menu.getMenu());
		menu.setOnMenuItemClickListener(this::onOptionsItemSelected);
		itemMap.put(menu.getMenu().getItem(0), pinView.getPin());
		itemMap.put(menu.getMenu().getItem(1), pinView.getPin());
		menu.show();
	}

	private void newPinMenu(View view) {
		PopupMenu menu = new PopupMenu(mainActivity, view);
		menu.getMenuInflater().inflate(R.menu.pins, menu.getMenu());
		menu.setOnMenuItemClickListener(this::onOptionsItemSelected);
		menu.show();
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
