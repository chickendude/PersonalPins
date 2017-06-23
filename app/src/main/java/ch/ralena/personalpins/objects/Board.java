package ch.ralena.personalpins.objects;

import java.util.List;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Board extends RealmObject {
	@PrimaryKey
	@Index
	private String id = UUID.randomUUID().toString();;

	private String title;
	private RealmList<Pin> pins;
	private String coverFilepath;

	public Board() {super();}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Pin> getPins() {
		return pins;
	}

	public void setPins(RealmList<Pin> pins) {
		this.pins = pins;
	}

	public String getCoverFilepath() {
		return coverFilepath;
	}

	public void setCoverFilepath(String coverFilepath) {
		this.coverFilepath = coverFilepath;
	}
}
