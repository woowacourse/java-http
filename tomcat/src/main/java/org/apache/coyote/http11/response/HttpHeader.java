package org.apache.coyote.http11.response;

import java.util.List;
import java.util.Map;

import org.apache.coyote.http11.common.HttpHeaderType;

public class HttpHeader {

	private static final String CRLF = " \r\n";

	private final Map<HttpHeaderType, List<String>> header;

	private HttpHeader(Map<HttpHeaderType, List<String>> header) {
		this.header = header;
	}

	public static HttpHeader of(HttpHeaderType key, String... value) {
		return new HttpHeader(Map.of(key, List.of(value)));
	}

	public String generateHeaderResponse() {
		StringBuilder sb = new StringBuilder();

		int count = 1;
		for (Map.Entry<HttpHeaderType, List<String>> header : header.entrySet()) {
			final String value = joinValues(header.getValue());
			sb.append(String.join(": ", header.getKey().getValue(), value)).append(CRLF);
		}
		return sb.toString();
	}

	private String joinValues(List<String> value) {
		if (value.size() > 1) {
			return String.join(";", value);
		}
		return value.get(0);
	}

	public Map<HttpHeaderType, List<String>> getHeader() {
		return header;
	}

}
