package org.apache.coyote.http11.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HttpRequest {

	private static final String START_LINE_DELIMITER = " ";

	private static final int HTTP_METHOD_INDEX = 0;
	private static final int HTTP_URL_INDEX = 1;

	private static final String EMPTY_BODY = "";

	private static final int PARAM_KEY_INDEX = 0;
	private static final int PARAM_VALUE_INDEX = 1;
	private static final int PARAMS_INDEX = 1;
	private static final String PARAM_KEY_VALUE_DELIMITER = "=";
	private static final String PARAM_DELIMITER = "&";
	private static final String QUERY_STRING_DELIMITER = "?";

	private static final String EXTENSION_DELIMITER = ".";

	private final HttpMethod method;
	private final String url;
	private final HttpHeaders httpHeaders;
	private final String body;

	public HttpRequest(BufferedReader requestReader) throws IOException {
		String startLine = requestReader.readLine();
		this.method = HttpMethod.from(parseStartLine(startLine, HTTP_METHOD_INDEX));
		this.url = parseStartLine(startLine, HTTP_URL_INDEX);
		this.httpHeaders = new HttpHeaders(requestReader);
		this.body = extractBody(requestReader);
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

	public String getUrl() {
		return url;
	}

	public boolean isStaticResourceRequest() {
		return url.contains(EXTENSION_DELIMITER);
	}

	public ContentType getContentType() {
		return ContentType.from(extractExtension());
	}

	private String extractExtension() {
		return url.split("\\" + EXTENSION_DELIMITER)[HTTP_URL_INDEX];
	}

	public String getQueryString(String key) {
		String contentType = httpHeaders.getValue(HttpHeader.CONTENT_TYPE);
		List<String> requestParams = extractRequestParams(contentType);
		return requestParams.stream()
			.filter(param -> param.split(PARAM_KEY_VALUE_DELIMITER)[PARAM_KEY_INDEX].equals(key))
			.map(param -> param.split(PARAM_KEY_VALUE_DELIMITER)[PARAM_VALUE_INDEX])
			.findAny()
			.orElse(EMPTY_BODY);
	}

	private List<String> extractRequestParams(String contentType) {
		List<String> requestParams = new ArrayList<>();
		if (ContentType.FORM_DATA.equals(contentType)) {
			String[] params = body.split(PARAM_DELIMITER);
			requestParams.addAll(List.of(params));
		}
		if (url.contains(QUERY_STRING_DELIMITER)) {
			String[] params = url.split("\\" + QUERY_STRING_DELIMITER)[PARAMS_INDEX]
				.split(PARAM_DELIMITER);
			requestParams.addAll(List.of(params));
		}
		return requestParams;
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
