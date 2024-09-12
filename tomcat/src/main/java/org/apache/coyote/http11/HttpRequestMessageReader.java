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
		String body = readBody(reader);

		return new HttpRequest(requestLine, headers, body);
	}

	private static String readBody(BufferedReader reader) throws IOException {
		List<String> result = new ArrayList<>();
		while (reader.ready()) {
			result.add(reader.readLine());
		}

		return String.join("\r\n", result);
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
