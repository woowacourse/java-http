package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;

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
			final var response = makeHttpResponse(request);

			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}

	private String parseHttpRequest(InputStream inputStream) throws IOException {
		BufferedReader requestReader = new BufferedReader(new InputStreamReader(inputStream));
		return requestReader.readLine();
	}

	private String makeHttpResponse(String request) throws IOException {
		String requestUri = extractRequestUri(request);
		if (requestUri.equals("/")) {
			return constructHttpMessage("Hello world!", 200);
		}
		if (requestUri.equals("/index.html")) {
			return constructHttpMessage(getHtml("static/index.html"), 200);
		}
		return constructHttpMessage(getHtml("static/404.html"), 404);
	}

	private String extractRequestUri(String request) {
		return request
			.split("\r\n")[0]
			.split(" ")[1];
	}

	private String getHtml(String path) throws IOException {
		final URL resource = getClass().getClassLoader().getResource(path);
		return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
	}

	private String constructHttpMessage(String responseBody, int statusCode) {
		String status = "";
		if (statusCode == 200) {
			status = " OK ";
		}
		if (statusCode == 404) {
			status = " Not Found ";
		}
		return String.join("\r\n",
			"HTTP/1.1 " + statusCode + status,
			"Content-Type: text/html;charset=utf-8 ",
			"Content-Length: " + responseBody.getBytes().length + " ",
			"",
			responseBody);
	}
}
