package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.http11.http.request.HttpRequest;

public class HttpRequestMessageReader {

	public static HttpRequest read(InputStream inputStream) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String requestLine = reader.readLine();
		List<String> headers = readHeaders(reader);
		String body = readBody(reader, getContentLength(headers));

		return new HttpRequest(requestLine, headers, body);
	}

	private static int getContentLength(List<String> headers) {
		return headers.stream()
				.filter(header -> header.startsWith("Content-Length"))
				.findFirst()
				.map(header -> Integer.parseInt(header.split(": ")[1].strip()))
				.orElse(0);
	}

	private static String readBody(BufferedReader reader, int contentLength) throws IOException {
		if (contentLength == 0) {
			return "";
		}

		char[] body = new char[contentLength];
		int readLength = reader.read(body, 0, contentLength);
		if (readLength != contentLength) {
			throw new IllegalArgumentException("Content-Length mismatch");
		}

		return new String(body);
	}

	private static List<String> readHeaders(BufferedReader reader) throws IOException {
		List<String> headers = new ArrayList<>();

		String line = reader.readLine();
		while (line != null && !line.isEmpty()) {
			headers.add(line);
			line = reader.readLine();
		}

		return headers;
	}
}
