package ch.ralena.personalpins;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	BottomNavigationView bottomNavigationView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

		bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
			switch (item.getItemId()) {
				case R.id.actionPins:
					break;
				case R.id.actionSettings:
					break;
				case R.id.actionTags:
					break;
			}
			return true;
		});
	}
}
