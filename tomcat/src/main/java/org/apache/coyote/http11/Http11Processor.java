package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;

import org.apache.HandlerMapping;
import org.apache.HomeRequestHandler;
import org.apache.HttpRequest;
import org.apache.LoginRequestHandler;
import org.apache.RegisterRequestHandler;
import org.apache.StaticResourceRequestHandler;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

	private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

	private final Socket connection;
	private final HandlerMapping handlerMapping;

	public Http11Processor(final Socket connection) {
		this.connection = connection;
		this.handlerMapping = new HandlerMapping();
		handlerMapping.register(new StaticResourceRequestHandler());
		handlerMapping.register(new HomeRequestHandler());
		handlerMapping.register(new LoginRequestHandler(SessionManager.getInstance()));
		handlerMapping.register(new RegisterRequestHandler());
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

			HttpRequest request = HttpRequest.from(inputStream);

			if (!request.isHttp11VersionRequest()) {
				throw new IOException("not http1.1 request");
			}

			var response = handlerMapping.getHandler(request).handle(request);

			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}
}
