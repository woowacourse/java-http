package org.apache.coyote.http11.response;

public class ResponseBody {

	private final String contentType;
	private final byte[] content;

	public static ResponseBody.Builder builder() {
		return new ResponseBody.Builder();
	}

	private ResponseBody(String contentType, byte[] content) {
		this.contentType = contentType;
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}

	public byte[] getContent() {
		return content;
	}

	public int getContentLength() {
		return content.length;
	}

	public static class Builder {

		private String contentType;
		private byte[] content;

		public Builder contentType(String contentType) {
			this.contentType = contentType;
			return this;
		}

		public Builder content(byte[] content) {
			this.content = content;
			return this;
		}

		public ResponseBody build() {
			return new ResponseBody(contentType, content);
		}
	}
}
