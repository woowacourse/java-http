package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.web.HttpResponse;
import com.techcourse.web.handler.RequestHandler;
import com.techcourse.web.handler.RequestHandlerMapper;

public class Http11Processor implements Runnable, Processor {

	private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

	private final Socket connection;
	private final RequestHandlerMapper handlerMapper;

	public Http11Processor(final Socket connection) {
		this.connection = connection;
		this.handlerMapper = RequestHandlerMapper.getInstance();
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

			HttpRequest request = HttpRequestReader.read(inputStream);
			RequestHandler handler = handlerMapper.findHandler(request.getRequestPath());
			HttpResponse response = handler.handle(request);
			String httpResponseMessage = response.createResponseMessage();

			outputStream.write(httpResponseMessage.getBytes(StandardCharsets.UTF_8));
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}
}
