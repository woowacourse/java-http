package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.RequestMapping;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			 final var requestReader = new BufferedReader(new InputStreamReader(inputStream))
		) {
			HttpRequest request = new HttpRequest(requestReader);
			HttpResponse response = new HttpResponse();
			log.info("REQUEST \r\n{}", request);

			Controller controller = RequestMapping.getController(request.getUrl());
			controller.service(request, response);

			log.info("RESPONSE \r\n{}", response);

			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
