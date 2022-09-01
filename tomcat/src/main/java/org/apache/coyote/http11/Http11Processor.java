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

			var response = "";

			String url = getUrl(inputStream);

			if (url == null)
				return;

			response = customController(response, url);

			outputStream.write(response.getBytes());
			outputStream.flush();

		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}

	private String customController(String response, String url) throws IOException {
		if (url.equals("/")) {
			var responseBody = "Hello world!";
			return String.join("\r\n",
				"HTTP/1.1 200 OK ",
				"Content-Type: text/html;charset=utf-8 ",
				"Content-Length: " + responseBody.getBytes().length + " ",
				"",
				responseBody);
		}

		if (url.startsWith("/index.html")) {
			return createResponse(url, "text/html");
		}

		if (url.startsWith("/css")) {
			return createResponse(url, "text/css");
		}

		if (url.startsWith("/js")) {
			return createResponse(url, "application/javascript");
		}

		if (url.startsWith("/assets")) {
			return createResponse(url, "application/javascript");
		}
		return response;
	}

	private String getUrl(InputStream inputStream) throws IOException {
		String line = new BufferedReader(new InputStreamReader(inputStream))
			.readLine();

		if (line == null) {
			return null;
		}

		return findUrl(line);
	}

	private String findUrl(String line) {
		return line.split(" ")[1];
	}

	private String createResponse(String url, String ContentType) throws IOException {
		String responseBody;
		String response;
		final String path = getStaticPath(url);
		final URL resource = getClass().getClassLoader().getResource(path);
		responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
		response = String.join("\r\n",
			"HTTP/1.1 200 OK ",
			"Content-Type: " + ContentType + ";charset=utf-8 ",
			"Content-Length: " + responseBody.getBytes().length + " ",
			"",
			responseBody);
		return response;
	}

	private String getStaticPath(String url) {
		return "static" + url;
	}
}

