package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.ControllerMapper;
import org.apache.coyote.http11.controller.Handler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
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
			 final var outputStream = connection.getOutputStream();
			 final var reader = new BufferedReader(new InputStreamReader(inputStream))) {

			final HttpRequest request = HttpRequest.from(reader);
			final Handler controller = ControllerMapper.findController(request.getUrl());
			final HttpResponse response = controller.handle(request);

			outputStream.write(response.generateHttpResponse().getBytes(StandardCharsets.UTF_8));
			outputStream.flush();

		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}
}

