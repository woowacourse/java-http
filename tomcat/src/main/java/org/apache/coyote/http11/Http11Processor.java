package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

	private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

	private static final String END_OF_LINE = "";
	private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
	private static final String RESOURCE_ROOT_PATH = "static";
	private static final String REQUEST_DELIMITER = " ";

	private final Socket connection;

	public Http11Processor(final Socket connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
		process(connection);
	}

	@Override
	public void process(final Socket connection) {
		try (final var inputStream = connection.getInputStream();
			 final var outputStream = connection.getOutputStream();
			 final var reader = new BufferedReader(new InputStreamReader(inputStream))) {
			final var requestHeader = readRequestHeader(reader);
			final var requestLine = requestHeader.get(0);
			final var method = requestLine.split(REQUEST_DELIMITER)[0];
			var uri = requestLine.split(REQUEST_DELIMITER)[1];

			final var index = uri.indexOf("?");
			var path = uri;
			final Map<String, String> queryParams = new HashMap<>();

			if (index != -1) {
				path = uri.substring(0, index);
				final var queryString = uri.substring(index + 1);
				final var split = queryString.split("&");
				for (final String s : split) {
					final var i = s.indexOf("=");
					queryParams.put(s.substring(0, i), s.substring(i + 1));
				}
			}

			if ("/login".equals(path)) {
				final var account = queryParams.get("account");
				final var password = queryParams.get("password");

				InMemoryUserRepository.findByAccount(account)
					.ifPresent(user -> {
						log.info(user.toString());
					});
			}

			final var headers = requestHeader.stream()
				.skip(1)
				.map(header -> header.split(": ", 2))
				.collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));

			var responseBody = getResponseBody(path);

			var contentType = "text/html;charset=utf-8";
			final var acceptHeader = headers.get("Accept");
			if (acceptHeader != null && acceptHeader.contains("text/css")) {
				contentType = "text/css";
			}

			final var response = String.join("\r\n",
				"HTTP/1.1 200 OK ",
				"Content-Type: " + contentType + " ",
				"Content-Length: " + responseBody.getBytes().length + " ",
				"",
				responseBody);

			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}

	private List<String> readRequestHeader(BufferedReader reader) throws IOException {
		final var header = new ArrayList<String>();

		String line;

		while ((line = reader.readLine()) != null && !END_OF_LINE.equals(line)) {
			header.add(line);
		}

		return header;
	}

	private String getResponseBody(String path) throws IOException {
		if ("/".equals(path)) {
			return DEFAULT_RESPONSE_BODY;
		}

		if (!path.contains(".")) {
			path += ".html";
		}

		final var resource = this.getClass().getClassLoader()
			.getResource(RESOURCE_ROOT_PATH + path);

		if (resource != null) {
			final var file = new File(resource.getPath());
			if (file.isFile()) {
				final var resourcePath = file.toPath();
				return new String(Files.readAllBytes(resourcePath));
			}
		}

		throw new IllegalArgumentException(path + "올바르지 않은 path");
	}
}
