package ch.ralena.personalpins;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		RealmConfiguration config = new RealmConfiguration.Builder()
				.name("personalpins.realm")
				.schemaVersion(1)
				.build();
		Realm.setDefaultConfiguration(config);
	}
}
