package ch.ralena.personalpins.objects;

import java.util.Objects;

import io.realm.RealmObject;

public class Tag extends RealmObject {
	private String title;

	public Tag() {super();	}

	public Tag(String title) {
		super();
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
		return Objects.equals(title, (((Tag) obj).title));
	}
}
