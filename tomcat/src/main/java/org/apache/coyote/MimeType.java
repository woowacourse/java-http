package org.apache.coyote;

public enum MimeType {
	HTML(".html", "text/html", true),
	CSS(".css", "text/css", true),
	JS(".js", "application/javascript", true),
	SVG(".svg", "image/svg+xml", false),
	;

	private final String fileExtension;
	private final String value;
	private final boolean isText;

	MimeType(final String fileExtension, final String value, final boolean isText) {
		this.fileExtension = fileExtension;
		this.value = value;
		this.isText = isText;
	}

	public static MimeType fromPath(final String path) {
		validatePath(path);
		for (final MimeType mimeType : values()) {
			if (path.endsWith(mimeType.fileExtension)) {
				return mimeType;
			}
		}
		throw new IllegalArgumentException("지원하지 않는 파일 확장자입니다.");
	}

	private static void validatePath(final String path) {
		if (path == null) {
			throw new IllegalArgumentException("파일 경로는 null일 수 없습니다.");
		}
		if (!path.contains(".")) {
			throw new IllegalArgumentException("올바르지 않은 파일 경로입니다.");
		}
	}

	public String getValue() {
		if (!isText) {
			return value;
		}
		return addCharsetParam(value);
	}

	private String addCharsetParam(String type) {
		return type + ";charset=utf-8";
	}
}
