package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.HttpHandlers;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

	private static final HttpHandlers HTTP_HANDLERS = new HttpHandlers();
	private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
			 final var outputStream = connection.getOutputStream()) {
			final String plainRequest = readRequest(inputStream);

			final HttpRequest request = HttpRequest.from(plainRequest);
			final HttpResponse response = HTTP_HANDLERS.handleTo(request);

			outputStream.write(response.buildResponse().getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}

	private static String readRequest(final InputStream inputStream) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		final StringBuilder builder = new StringBuilder();

		for (String readLine = reader.readLine();
			 readLine != null && !readLine.isEmpty();
			 readLine = reader.readLine()) {
			builder.append(readLine);
		}
		return builder.toString();
	}
}
