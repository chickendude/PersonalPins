package ch.ralena.personalpins.objects;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Objects;

@Table(name = "tags")
public class Tag extends Model {
	@Column(name = "title")
	private String title;

	public Tag() {super();	}

	public Tag(String title) {
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
