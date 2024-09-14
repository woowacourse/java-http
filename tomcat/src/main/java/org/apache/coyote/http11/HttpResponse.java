package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HttpResponse {

	private StatusLine statusLine;
	private List<String> headers = new ArrayList<>();
	private byte[] responseBody;

	private HttpResponse() {
	}

	public static HttpResponse empty() {
		return new HttpResponse();
	}

	public void ok(String uri, byte[] response) {
		setStatusLine(StatusLine.from(HttpStatus.OK));
		setContentType(uri);
		setResponseBody(response);
		setContentLength();
	}

	public void redirect(String uri, byte[] response, String location) {
		setStatusLine(StatusLine.from(HttpStatus.FOUND));
		setContentType(uri);
		setResponseBody(response);
		setContentLength();
		setLocation("/" + location);
	}

	public void setStatusLine(StatusLine statusLine) {
		this.statusLine = statusLine;
	}

	public void addHeaders(List<String> headers) {
		this.headers = headers;
	}

	public void setContentLength() {
		if (responseBody == null) {
			throw new IllegalArgumentException("no response body");
		}
		headers.add("Content-Length: " + responseBody.length + " ");
	}

	public void setResponseBody(byte[] responseBody) {
		this.responseBody = responseBody;
	}

	private static int calculateContentLength(String content) {
		return content.getBytes(StandardCharsets.UTF_8).length;
	}

	public void setContentType(String uri) {
		headers.add("Content-Type: " + getContentType(uri) + ";charset=utf-8 ");
	}

	private static String getContentType(String uri) {
		if (uri.startsWith("/css")) {
			return "text/css";
		}
		return "text/html";
	}

	public static void methodNotAllowed() {
		throw new IllegalArgumentException("method Not Allowed");
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

	public void setLocation(String redirectUri) {
		headers.add("Location: " + redirectUri);
	}
}
