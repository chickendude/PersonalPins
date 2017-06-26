package ch.ralena.personalpins;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.File;

public class FullScreenImageActivity extends AppCompatActivity {
	public static final String EXTRA_IMAGE_FILEPATH = "extra_image_filepath";
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_full_screen_image);

		PhotoView image = (PhotoView) findViewById(R.id.image);
		image.setOnPhotoTapListener((view, x, y) -> finish());

		String imageFilepath = getIntent().getStringExtra(EXTRA_IMAGE_FILEPATH);
		Uri imageUri = Uri.fromFile(new File(imageFilepath));
		Picasso.with(this)
				.load(imageUri)
				.into(image);
	}
}
