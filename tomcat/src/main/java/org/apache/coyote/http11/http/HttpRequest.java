package org.apache.coyote.http11.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public class HttpRequest {

	private static final String START_LINE_DELIMITER = " ";

	private static final int HTTP_METHOD_INDEX = 0;
	private static final int HTTP_URL_INDEX = 1;
	private static final String EMPTY_BODY = "";

	private static final int EXTENSION_INDEX = 1;
	private static final String EXTENSION_DELIMITER = ".";

	private static final int SPLIT_SUCCESS_LENGTH = 2;

	private static final String PARAM_DELIMITER = "&";

	private final HttpMethod method;
	private final String url;
	private final HttpHeaders httpHeaders;
	private final String body;
	private final RequestParams requestParams;

	public HttpRequest(BufferedReader requestReader) throws IOException {
		String startLine = requestReader.readLine();
		this.method = HttpMethod.from(parseStartLine(startLine, HTTP_METHOD_INDEX));
		this.url = parseStartLine(startLine, HTTP_URL_INDEX);
		this.httpHeaders = new HttpHeaders(requestReader);
		this.body = extractBody(requestReader);
		this.requestParams = new RequestParams(getContentType(), url, body);
	}

	private String parseStartLine(String startLine, int index) {
		return startLine.split(START_LINE_DELIMITER)[index];
	}

	private String extractBody(BufferedReader requestReader) throws IOException {
		Optional<String> length = httpHeaders.findValue(HttpHeader.CONTENT_LENGTH);
		if (length.isEmpty()) {
			return EMPTY_BODY;
		}
		int contentLength = Integer.parseInt(length.get());
		char[] body = new char[contentLength];
		requestReader.read(body, HTTP_METHOD_INDEX, contentLength);
		return new String(body);
	}

	public boolean isStaticResourceRequest() {
		return url.contains(EXTENSION_DELIMITER);
	}

	public String getQueryString(String key) {
		return requestParams.getOrDefault(key);
	}

	public ContentType getContentType() {
		if (!body.isEmpty() && body.contains(PARAM_DELIMITER)) {
			return ContentType.FORM_DATA;
		}
		return ContentType.from(extractExtension());
	}

	private String extractExtension() {
		String[] split = url.split("\\" + EXTENSION_DELIMITER);
		if (split.length == SPLIT_SUCCESS_LENGTH) {
			return split[EXTENSION_INDEX];
		}
		return EMPTY_BODY;
	}

	public String getUrl() {
		return url;
	}

	public String getMethod() {
		return method.name();
	}

	@Override
	public String toString() {
		return "===HttpRequest===" + "\r\n" +
			"method='" + method + "\r\n" +
			"url='" + url + "\r\n" +
			httpHeaders + "\r\n" +
			"body='" + body + "\r\n" +
			'}';
	}
}
