package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

	private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

	private final Socket connection;
	private static final Map<String, String> httpRequestHeader = new HashMap<>();
	private static final String sessionId = "JSESSIONID=sessionId";

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

			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			HttpRequest httpRequest = new HttpRequest(reader);

			RequestHandler requestHandler = new RequestHandler(httpRequest, outputStream);
			requestHandler.handleRequest();

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
