package ch.ralena.personalpins;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.facebook.stetho.Stetho;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.ralena.personalpins.objects.Pin;
import ch.ralena.personalpins.objects.Tag;
import ch.ralena.personalpins.sql.SqlManager;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SQLitePinTest {
	private SqlManager sqlManager;

	@Before
	public void createDb() {
		sqlManager = new SqlManager(InstrumentationRegistry.getTargetContext());
		Stetho.initializeWithDefaults(InstrumentationRegistry.getTargetContext());
		sqlManager.open();
	}

	@After
	public void closeDb() {
		sqlManager.close();
	}

	@Test
	public void addPin() {
		// arrange
		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag("cool"));
		tags.add(new Tag("fun"));
		Pin pin = new Pin(0, "Test Pin", Pin.TYPE_PICTURE, "note", "/home/coolstuff", tags);

		// act
		sqlManager.insertPin(pin);
		List<Pin> pins = sqlManager.getPins();
		Pin pin2 = pins.get(pins.size()-1);

		//assert
		assertEquals(pin, pin2);
	}
}
