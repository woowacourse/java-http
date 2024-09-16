package org.apache.coyote.http11;

public class RequestLine {

	private static final String PROTOCOL_VERSION = "HTTP/1.1";
	private static final String SPLIT_REGEX = " ";
	private static final int METHOD_INDEX = 0;
	private static final int PATH_INDEX = 1;
	private static final int VERSION_INDEX = 2;

	private final HttpMethod method;
	private final String path;
	private final String version;

	private RequestLine(HttpMethod method, String path, String version) {
		this.method = method;
		this.path = path;
		this.version = version;
	}

	public static RequestLine from(String line) {
		if (line.isEmpty()) {
			throw new IllegalArgumentException("Request line is empty");
		}

		String[] tokens = parseLine(line);

		return new RequestLine(
			HttpMethod.from(tokens[METHOD_INDEX]),
			tokens[PATH_INDEX],
			tokens[VERSION_INDEX]
		);
	}

	private static String[] parseLine(String line) {
		return line.split(SPLIT_REGEX);
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getVersion() {
		return version;
	}

	public boolean isHttp11Version() {
		return version.equals(PROTOCOL_VERSION);
	}
}
