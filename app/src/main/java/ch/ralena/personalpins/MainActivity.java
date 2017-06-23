package ch.ralena.personalpins;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import ch.ralena.personalpins.fragments.BoardFragment;
import ch.ralena.personalpins.fragments.PinsFragment;
import ch.ralena.personalpins.fragments.TagsFragment;
import ch.ralena.personalpins.fragments.UserFragment;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
	private static final int REQUEST_READ_EXTERNAL = 11;
	public AppBarLayout appBarLayout;
	private BottomNavigationView bottomNavigationView;

	FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

		Realm.init(this);

		fragmentManager = getSupportFragmentManager();
		bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
		bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
			fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			switch (item.getItemId()) {
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

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_READ_EXTERNAL) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

			} else {
				Toast.makeText(this, "Sorry, we need read permission to continue", Toast.LENGTH_SHORT).show();
				finishAffinity();
			}
		}
	}

	private void replaceFragment(Fragment fragment) {
		fragmentManager.beginTransaction()
				.replace(R.id.frameContainer, fragment)
				.commit();
	}

	public void showActionBar() {
		appBarLayout.setExpanded(true);
	}
	public void hideActionBar() {
		appBarLayout.setExpanded(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			getSupportFragmentManager().popBackStack();
		}
		return super.onOptionsItemSelected(item);
	}

}
