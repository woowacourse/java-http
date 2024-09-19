package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponsePrinter;
import org.apache.coyote.http11.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
	private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

	private final Socket connection;
	private final ServletContainer container;

	public Http11Processor(final Socket connection, final ServletContainer container) {
		this.connection = connection;
		this.container = container;
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
			HttpRequest request = new HttpRequest(reader);
			HttpResponse response = new HttpResponse();

			container.invoke(request, response);

			new ResponsePrinter(outputStream).print(response);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
