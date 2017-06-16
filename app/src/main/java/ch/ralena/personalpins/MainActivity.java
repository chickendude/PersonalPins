package ch.ralena.personalpins;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.facebook.stetho.Stetho;

import ch.ralena.personalpins.fragments.BoardFragment;
import ch.ralena.personalpins.fragments.PinsFragment;
import ch.ralena.personalpins.fragments.TagsFragment;
import ch.ralena.personalpins.fragments.UserFragment;

public class MainActivity extends AppCompatActivity {
	BottomNavigationView bottomNavigationView;

	FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Stetho.initializeWithDefaults(this);

		fragmentManager = getSupportFragmentManager();
		bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
		bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
			switch(item.getItemId()) {
				case R.id.actionBoard:
					replaceFragment(new BoardFragment());
					break;
				case R.id.actionPins:
					replaceFragment(new PinsFragment());
					break;
				case R.id.actionTags:
					replaceFragment(new TagsFragment());
					break;
				case R.id.actionUser:
					replaceFragment(new UserFragment());
					break;
			}
			return true;
		});
		bottomNavigationView.setSelectedItemId(R.id.actionBoard);
	}

	private void replaceFragment(Fragment fragment) {
		fragmentManager.beginTransaction()
				.replace(R.id.frameContainer, fragment)
				.commit();
	}
}
