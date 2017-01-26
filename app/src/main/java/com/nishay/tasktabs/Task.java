package com.nishay.tasktabs;

public class Task {
	
	private long id;
	private String description;
	private String category;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String d) {
		this.description = d;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String newCat) {
		this.category = newCat;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return description;
	}


}
