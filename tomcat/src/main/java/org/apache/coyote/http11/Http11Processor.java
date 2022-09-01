package org.apache.coyote.http11;

import static java.util.stream.Collectors.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {

	private static final String UNAUTHORIZED_HTML = "static/401.html";
	private static final String NOT_FOUND_HTML = "static/404.html";

	private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

	private final Socket connection;

	public Http11Processor(final Socket connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		process(connection);
	}

	@Override
	public void process(final Socket connection) {
		try (final var inputStream = connection.getInputStream();
			 final var outputStream = connection.getOutputStream()) {

			final var request = parseHttpRequest(inputStream);
			final var response = makeHttpResponse(request);

			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}

	private String parseHttpRequest(InputStream inputStream) throws IOException {
		BufferedReader requestReader = new BufferedReader(new InputStreamReader(inputStream));
		List<String> requestLines = new ArrayList<>();
		while (requestReader.ready()) {
			requestLines.add(requestReader.readLine());
		}
		log.info("\r\n" + String.join("\r\n", requestLines));
		return requestLines.get(0);
	}

	private HttpResponse makeHttpResponse(String request) throws IOException {
		String requestUri = extractRequestUri(request);
		if (requestUri.equals("/")) {
			return new HttpResponse(StatusCode.OK, "Hello world!", ContentType.HTML);
		}

		String extension = extractExtension(requestUri);
		if (!extension.equals("")) {
			return handleStaticRequest(requestUri, extension);
		}

		return handleApiRequest(requestUri);
	}

	private String extractRequestUri(String request) {
		return request
			.split("\r\n")[0]
			.split(" ")[1];
	}

	private String extractExtension(String requestUri) {
		if (requestUri.contains(".")) {
			return requestUri.split("\\.")[1];
		}
		return "";
	}

	private HttpResponse handleStaticRequest(String requestUri, String extension) throws IOException {
		ContentType contentType = ContentType.from(extension);
		try {
			return makeHttpResponseWithHtml("static/" + requestUri, 200, contentType);
		} catch (NullPointerException e) {
			return makeHttpResponseWithHtml(NOT_FOUND_HTML, 404, ContentType.HTML);
		}
	}

	private HttpResponse makeHttpResponseWithHtml(
		String htmlPath, int statusCode, ContentType contentType
	) throws IOException {
		final URL resource = getClass().getClassLoader().getResource(htmlPath);
		String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
		return new HttpResponse(StatusCode.from(statusCode), responseBody, contentType);
	}

	private HttpResponse handleApiRequest(String requestUri) throws IOException {
		if (requestUri.startsWith("/login")) {
			Map<String, String> queryStrings = parseQueryString(requestUri);
			return checkValidLogin(queryStrings.get("account"), queryStrings.get("password"));
		}
		return makeHttpResponseWithHtml(NOT_FOUND_HTML, 404, ContentType.HTML);
	}

	private Map<String, String> parseQueryString(String requestUri) {
		return Arrays.stream(
				requestUri.split("\\?")[1]
					.split("&")
			)
			.collect(toMap(
				value -> value.split("=")[0],
				value -> value.split("=")[1])
			);
	}

	private HttpResponse checkValidLogin(String account, String password) throws IOException {
		Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
		if (findUser.isPresent() && findUser.get().checkPassword(password)) {
			return new HttpResponse(StatusCode.OK, findUser.get().toString(), ContentType.HTML);
		}
		return makeHttpResponseWithHtml(UNAUTHORIZED_HTML, 401, ContentType.HTML);
	}
}
