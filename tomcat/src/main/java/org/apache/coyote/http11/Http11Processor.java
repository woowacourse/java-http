package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

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
			final var response = handleStaticResource(request);

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

	private HttpResponse handleStaticResource(String request) throws IOException {
		String requestUri = extractRequestUri(request);
		if (requestUri.equals("/")) {
			return new HttpResponse(StatusCode.OK, "Hello world!", ContentType.HTML);
		}
		ContentType contentType = ContentType.from(extractExtension(requestUri));
		return handleNotFoundHtml("static/" + requestUri, contentType);
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

	private HttpResponse handleNotFoundHtml(String path, ContentType contentType) throws IOException {
		try {
			return makeHttpResponseWithHtml(path, 200, contentType);
		} catch (NullPointerException e) {
			return makeHttpResponseWithHtml("static/404.html", 404, ContentType.HTML);
		}
	}

	private HttpResponse makeHttpResponseWithHtml(String htmlPath, int statusCode, ContentType contentType) throws IOException {
		final URL resource = getClass().getClassLoader().getResource(htmlPath);
		String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
		return new HttpResponse(StatusCode.from(statusCode), responseBody, contentType);
	}
}
