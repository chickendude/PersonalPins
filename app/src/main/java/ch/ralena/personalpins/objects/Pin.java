package ch.ralena.personalpins.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pin {
	public static final String TYPE_VIDEO = "video";
	public static final String TYPE_PICTURE = "picture";

	private long id;
	private String title;
	private String type;
	private String note;
	private String filepath;
	private List<Tag> tags;

	public Pin(long id, String title, String type, String note, String filepath, List<Tag> tags) {
		this.title = title;
		this.type = type;
		this.tags = tags;
		this.filepath = filepath;
	}

	public Pin(String title, String type, String filepath) {
		this.title = title;
		this.type = type;
		this.filepath = filepath;
		tags = new ArrayList<>();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj.getClass() != this.getClass())
			return false;
		// fields

		Pin pin = (Pin) obj;
		return Objects.equals(title, pin.title) &&
				Objects.equals(type, pin.type) &&
				Objects.equals(note, pin.note) &&
				Objects.equals(filepath, pin.filepath) &&
				Objects.equals(tags, pin.tags);
	}
}
