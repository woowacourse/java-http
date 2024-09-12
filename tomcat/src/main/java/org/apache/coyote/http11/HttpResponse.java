package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HttpResponse {

	private final StatusLine statusLine;
	private final List<String> headers;
	private final byte[] responseBody;

	public HttpResponse(StatusLine statusLine, List<String> headers, byte[] responseBody) {
		this.statusLine = statusLine;
		this.headers = headers;
		this.responseBody = responseBody;
	}

	public static HttpResponse ok(String uri, String responseBody) {
		List<String> headers = new ArrayList<>();
		headers.add("Content-Type: " + getContentType(uri) + ";charset=utf-8 ");
		headers.add("Content-Length: " + calculateContentLength(responseBody) + " ");

		return new HttpResponse(StatusLine.from(HttpStatus.OK), headers, responseBody.getBytes(StandardCharsets.UTF_8));
	}

	public static HttpResponse okWithContentType(byte[] responseBody, String contentType) {
		List<String> headers = new ArrayList<>();
		headers.add("Content-Type: " + contentType + " ");
		headers.add("Content-Length: " + responseBody.length + " ");

		return new HttpResponse(StatusLine.from(HttpStatus.OK), headers, responseBody);
	}

	public static HttpResponse redirect(String uri, String redirectUri) {
		List<String> headers = new ArrayList<>();
		headers.add("Content-Type: " + getContentType(uri) + ";charset=utf-8 ");
		headers.add("Content-Length: " + calculateContentLength(redirectUri) + " ");
		headers.add("Location: " + redirectUri);

		return new HttpResponse(StatusLine.from(HttpStatus.FOUND), headers,
			redirectUri.getBytes(StandardCharsets.UTF_8));
	}

	public static HttpResponse notFound() {
		List<String> headers = new ArrayList<>();
		headers.add("Content-Type: text/html; charset=UTF-8");
		String body = "<h1>404 - Not Found</h1>";
		headers.add("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length);

		return new HttpResponse(StatusLine.from(HttpStatus.NOT_FOUND), headers, body.getBytes(StandardCharsets.UTF_8));
	}

	private static int calculateContentLength(String content) {
		return content.getBytes(StandardCharsets.UTF_8).length;
	}

	private static String getContentType(String uri) {
		if (uri.startsWith("/css")) {
			return "text/css";
		}
		return "text/html";
	}

	public void setCookie(String key, String value) {
		for (String header : headers) {
			if (header.startsWith("Set-Cookie")) {
				String newCookie = header + " " + key + "=" + value + ";";
				headers.add(newCookie);
				headers.remove(header);
				return;
			}
		}
		headers.add("Set-Cookie: " + key + "=" + value + ";");
	}

	public List<String> getHeaders() {
		return Collections.unmodifiableList(headers);
	}

	public String getResponseBody() {
		return new String(responseBody, StandardCharsets.UTF_8);
	}

	public byte[] getBytes() {
		return toString().getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public String toString() {
		String statusLineString = statusLine.getLine() + " \r\n";
		String headersString = String.join("\r\n", headers);
		String bodyString = new String(responseBody, StandardCharsets.UTF_8);
		return statusLineString + headersString + "\r\n\r\n" + bodyString;
	}
}
