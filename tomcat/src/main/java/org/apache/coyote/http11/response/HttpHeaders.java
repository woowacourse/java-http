package org.apache.coyote.http11.response;

import java.util.List;
import java.util.stream.Collectors;

public class HttpHeaders {

	private final List<HttpHeader> httpHeaders;

	private HttpHeaders(List<HttpHeader> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	public static HttpHeaders from(HttpHeader... headers) {
		return new HttpHeaders(List.of(headers));
	}

	public String generateHeadersResponse() {
		return httpHeaders.stream()
			.map(HttpHeader::generateHeaderResponse)
			.collect(Collectors.joining());
	}
}
