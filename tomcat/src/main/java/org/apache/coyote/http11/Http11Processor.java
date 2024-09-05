package org.apache.coyote.http11;

import com.techcourse.Controller;
import com.techcourse.FrontController;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

public class Http11Processor implements Runnable, Processor {

	private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

	private final Socket connection;

	private final FrontController frontController;

	public Http11Processor(final Socket connection) {
		this.connection = connection;
		this.frontController = new FrontController();
	}

	@Override
	public void run() {
		log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
		process(connection);
	}

	@Override
	public void process(final Socket connection) {
		try (final var inputStream = connection.getInputStream();
			 final var outputStream = connection.getOutputStream()) {

			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			List<String> headers = new ArrayList<>();
			String line;
			int contentLength = 0;

			while ((line = reader.readLine()) != null && !line.isEmpty()) {
				headers.add(line);

				if (line.startsWith("Content-Length")) {
					contentLength = Integer.parseInt(line.split(":")[1].trim());
				}
			}

			StringTokenizer tokenizer = new StringTokenizer(headers.get(0));
			String method = tokenizer.nextToken();
			String uri = tokenizer.nextToken();
			String httpVersion = tokenizer.nextToken();

			if (!httpVersion.equals("HTTP/1.1")) {
				throw new IOException("not http1.1 request");
			}

			var response = "";
			if (method.equals("GET")) {
				response = processGetRequest(uri);
			}
			if (method.equals("POST")) {
				char[] body = new char[contentLength];
				if (contentLength > 0) {
					reader.read(body, 0, contentLength);
				}

				String requestBody = new String(body);
				String decodedBody = URLDecoder.decode(requestBody, StandardCharsets.UTF_8);

				log.info("Decoded Request Body: {}", decodedBody);
				response = processPostRequest(uri, decodedBody);
			}

			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}

	private String processGetRequest(String uri) throws IOException {
		return frontController.processGetRequest(uri);
	}

	private String processPostRequest(String uri, String body) {
		Map<String, String> params = parseQueryString(body);

		return frontController.processPostRequest(uri, params);
	}

	private Map<String, String> parseQueryString(String queryString) {
		Map<String, String> parameters = new HashMap<>();
		String[] pairs = queryString.split("&");

		for (String pair : pairs) {
			String[] keyValue = pair.split("=", 2);

			String key = keyValue[0];
			String value = keyValue.length > 1 ? keyValue[1] : "";

			parameters.put(key, value);
		}

		return parameters;
	}
}
