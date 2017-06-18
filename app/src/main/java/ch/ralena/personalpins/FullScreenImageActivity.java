package ch.ralena.personalpins;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class FullScreenImageActivity extends AppCompatActivity {
	public static final String EXTRA_IMAGE_URI = "extra_image_uri";
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_full_screen_image);

		PhotoView image = (PhotoView) findViewById(R.id.image);
		image.setOnPhotoTapListener(new OnPhotoTapListener() {
			@Override
			public void onPhotoTap(ImageView view, float x, float y) {
				finish();
			}
		});

		String imageUri = getIntent().getStringExtra(EXTRA_IMAGE_URI);
		Picasso.with(this)
				.load(imageUri)
				.into(image);
	}
}
