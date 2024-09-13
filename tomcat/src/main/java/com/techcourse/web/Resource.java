package com.techcourse.web;

public class Resource {

	private final String contentType;
	private final byte[] content;

	public Resource(String contentType, byte[] content) {
		this.contentType = contentType;
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}

	public byte[] getContent() {
		return content;
	}
}
