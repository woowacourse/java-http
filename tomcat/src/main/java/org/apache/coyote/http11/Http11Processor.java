package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.HttpHandler;
import org.apache.coyote.http11.handler.NotFoundHandler;
import org.apache.coyote.http11.handler.RootHandler;
import org.apache.coyote.http11.handler.StaticResourceHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

	private static final List<HttpHandler> handlers = List.of(
		new RootHandler(),
		new StaticResourceHandler()
	);
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
			final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			final StringBuilder builder = new StringBuilder();
			while (reader.ready()) {
				builder.append(reader.readLine());
				builder.append(System.lineSeparator());
			}

			final HttpRequest request = HttpRequest.from(builder.toString());

			final HttpResponse response = handlers.stream()
				.filter(handler -> handler.isSupported(request))
				.findAny()
				.orElseGet(NotFoundHandler::new)
				.handleTo(request);

			outputStream.write(response.buildResponse().getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}
}
