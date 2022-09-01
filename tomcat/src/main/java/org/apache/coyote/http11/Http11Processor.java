package org.apache.coyote.http11;

import static org.apache.coyote.http11.UrlGenerator.getUrl;

import java.io.IOException;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

	private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

	private final Socket connection;
	private final Controller controller;

	public Http11Processor(final Socket connection) {
		this.connection = connection;
		this.controller = new Controller(new Response(), new UserService(log));
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
			response = controller.run(response, url);

			outputStream.write(response.getBytes());
			outputStream.flush();

		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}
}

