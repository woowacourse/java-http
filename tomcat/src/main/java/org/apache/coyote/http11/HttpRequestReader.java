package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestReader {

	public static HttpRequest read(InputStream inputStream) throws IOException {
		InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		BufferedReader br = new BufferedReader(reader);

		List<String> httpRequest = new ArrayList<>();

		String line;
		int contentLength = 0;

		while ((line = br.readLine()) != null && !line.isEmpty()) {
			httpRequest.add(line);

			if (line.startsWith("Content-Length:")) {
				contentLength = Integer.parseInt(line.split(":")[1].strip());
			}
		}

		if (contentLength > 0) {
			httpRequest.add("");
			char[] body = new char[contentLength];
			int numberOfCharacterRead = br.read(body, 0, contentLength);
			String requestBody = new String(body, 0, numberOfCharacterRead);
			httpRequest.add(requestBody);
		}

		return new HttpRequest(httpRequest);
	}
}
