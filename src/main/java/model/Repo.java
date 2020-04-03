package model;

import java.util.List;

public class Repo {
	
	private int id;
	private String title;
	private int star;
	private String description;
	private List<String> langs;
	private List<String> tags;

	@Override
	public String toString() {
		return "Repo [id=" + id + ", title=" + title + ", star=" + star + ", description=" + description + ", langs="
				+ langs + ", tags=" + tags + "]";
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getStar() {
		return star;
	}
	public void setStar(int star) {
		this.star = star;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getLangs() {
		return langs;
	}
	public void setLangs(List<String> lang) {
		this.langs = lang;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tag) {
		this.tags = tag;
	}

}
