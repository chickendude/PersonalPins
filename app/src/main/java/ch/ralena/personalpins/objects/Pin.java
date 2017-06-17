package ch.ralena.personalpins.objects;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Pin extends RealmObject {
	public static final String TYPE_VIDEO = "video";
	public static final String TYPE_PICTURE = "picture";

	private String title;
	private String type;
	private String note;
	private String filepath;
	private RealmList<Tag> tags;

	public Pin() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(RealmList<Tag> tags) {
		this.tags = tags;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
}
