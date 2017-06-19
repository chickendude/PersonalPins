package ch.ralena.personalpins;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

public class FullScreenVideoActivity extends AppCompatActivity {
	public static final String EXTRA_VIDEO_URI = "extra_video_uri";
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_full_screen_video);

		String videoUri = getIntent().getStringExtra(EXTRA_VIDEO_URI);
		VideoView videoView = (VideoView) findViewById(R.id.video);
		videoView.setVideoURI(Uri.parse(videoUri));
		videoView.start();
		videoView.setOnCompletionListener(MediaPlayer::start);
	}
}
