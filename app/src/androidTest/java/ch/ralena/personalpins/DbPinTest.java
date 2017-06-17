package ch.ralena.personalpins;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.activeandroid.ActiveAndroid;
import com.facebook.stetho.Stetho;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.ralena.personalpins.objects.Pin;
import ch.ralena.personalpins.objects.Tag;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DbPinTest {

	@Before
	public void setup() {
		Stetho.initializeWithDefaults(InstrumentationRegistry.getTargetContext());
		ActiveAndroid.initialize(InstrumentationRegistry.getTargetContext());
	}

	@Test
	public void addPin() {
		// arrange
		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag("cool"));
		tags.add(new Tag("fun"));
		Pin pin = new Pin(0, "Test Pin", Pin.TYPE_PICTURE, "note", "/home/coolstuff", tags);

		// act
		pin.save();
		Pin pin2 = Pin.load(Pin.class, pin.getId());

		//assert
		assertEquals(pin, pin2);
	}
}
