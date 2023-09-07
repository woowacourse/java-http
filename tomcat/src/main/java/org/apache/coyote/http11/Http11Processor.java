package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.apache.catalina.HandlerMapping;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

	private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

	private final Socket connection;
	private final HandlerMapping handlerMapping;

	public Http11Processor(final Socket connection, final HandlerMapping handlerMapping) {
		this.connection = connection;
		this.handlerMapping = handlerMapping;
	}

	@Override
	public void run() {
		log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
		process(connection);
	}

	@Override
	public void process(final Socket connection) {
		try (final var inputBuffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			 final var outputBuffer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
			final var requestReader = new Http11RequestReader(inputBuffer);
			final var requestWriter = new Http11ResponseWriter(outputBuffer);

			final var request = requestReader.read();
			requestWriter.write(handlerMapping.handle(request));
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}
}
