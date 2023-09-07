package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.HttpControllers;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequest.HttpRequestBuilder;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

	private static final HttpControllers HTTP_HANDLERS = new HttpControllers();
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
			final HttpRequest request = readRequest(inputStream);

			final HttpResponse response = HTTP_HANDLERS.handleTo(request);

			outputStream.write(response.buildResponse().getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}

	private static HttpRequest readRequest(final InputStream inputStream) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		final String requestHeader = readRequestHeader(reader);
		final HttpRequestBuilder builder = HttpRequestBuilder.from(requestHeader);
		final String requestBody = readRequestBody(reader, builder);

		return builder
			.body(requestBody)
			.build();
	}

	private static String readRequestBody(BufferedReader reader, HttpRequestBuilder builder) throws IOException {
		final Integer contentLength = builder.bodyLength();
		final char[] buffer = new char[contentLength];
		reader.read(buffer, 0, contentLength);
		final String requestBody = new String(buffer);
		return requestBody;
	}

	private static String readRequestHeader(final BufferedReader reader) throws IOException {
		final StringBuilder builder = new StringBuilder();
		for (String readLine = reader.readLine();
			 readLine != null && !readLine.isEmpty();
			 readLine = reader.readLine()) {
			builder.append(readLine);
			builder.append("\r\n");
		}
		return builder.toString();
	}
}
