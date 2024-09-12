package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.web.handler.Handler;
import com.techcourse.web.handler.HandlerMapper;

public class Http11Processor implements Runnable, Processor {

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

			String httpResponseMessage = createHttpResponseMessage(inputStream);

			outputStream.write(httpResponseMessage.getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}

	private String createHttpResponseMessage(InputStream inputStream) throws IOException {
		HttpRequest request = HttpRequestMessageReader.read(inputStream);
		log.info("request: {}", request);
		Handler handler = HandlerMapper.findHandler(request);
		HttpResponse response = handler.handle(request);

		return response.toResponseMessage();
	}
}
