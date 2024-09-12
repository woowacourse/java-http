package org.apache;

import java.util.Arrays;

public enum HttpHeader {

	// 요청(Request) 헤더
	HOST("Host"),
	USER_AGENT("User-Agent"),
	ACCEPT("Accept"),
	ACCEPT_ENCODING("Accept-Encoding"),
	ACCEPT_LANGUAGE("Accept-Language"),
	AUTHORIZATION("Authorization"),
	COOKIE("Cookie"),
	CONTENT_TYPE("Content-Type"),
	CONTENT_LENGTH("Content-Length"),
	ORIGIN("Origin"),
	REFERER("Referer"),
	CONNECTION("Connection"),

	// 응답(Response) 헤더
	SERVER("Server"),
	SET_COOKIE("Set-Cookie"),
	CONTENT_DISPOSITION("Content-Disposition"),
	CACHE_CONTROL("Cache-Control"),
	EXPIRES("Expires"),
	LOCATION("Location"),
	LAST_MODIFIED("Last-Modified"),
	TRANSFER_ENCODING("Transfer-Encoding"),
	ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"),

	// 공통 헤더
	DATE("Date"),
	UPGRADE("Upgrade"),
	WARNING("Warning");

	private final String headerName;

	HttpHeader(String headerName) {
		this.headerName = headerName;
	}

	public String getHeaderName() {
		return headerName;
	}

	@Override
	public String toString() {
		return headerName;
	}

	public static HttpHeader from(String headerName) {
		return Arrays.stream(values())
			.filter(header -> header.getHeaderName().equalsIgnoreCase(headerName))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Unknown header: " + headerName));
	}
}
