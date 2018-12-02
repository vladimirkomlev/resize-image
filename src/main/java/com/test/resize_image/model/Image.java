package com.test.resize_image.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "image")
public class Image {

	@Id
	private String id;

	private long index;

	private String name;

	private String size;

	private String expansion;

	public Image() {
		super();
	}

	public Image(String name, String size, long index, String expansion) {
		super();
		this.name = name;
		this.size = size;
		this.index = index;
		this.expansion = expansion;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSize() {
		return size;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public String getExpansion() {
		return expansion;
	}

	public void setExpansion(String expansion) {
		this.expansion = expansion;
	}

	@Override
	public String toString() {
		return "Image [id=" + id + ", name=" + name + ", size=" + size + ", index=" + index + ", expansion=" + expansion
				+ "]";
	}
}
