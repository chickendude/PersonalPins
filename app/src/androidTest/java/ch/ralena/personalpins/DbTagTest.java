package ch.ralena.personalpins;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.activeandroid.ActiveAndroid;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.ralena.personalpins.objects.Tag;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DbTagTest {
	Tag tag;
	@Before
	public void setup() {
		ActiveAndroid.initialize(InstrumentationRegistry.getTargetContext());
	}

	@After
	public void cleanup() {

	}

	@Test
	public void createTagTest() {
		// arrange
		tag = new Tag("Cool");

		// act
		tag.save();
		long id = tag.getId();

		Tag tag2 = Tag.load(Tag.class, id);

		// assert
		assertEquals(tag, tag2);

	}
}
