package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;

import org.apache.RequestMapping;
import org.apache.HomeController;
import org.apache.LoginController;
import org.apache.RegisterController;
import org.apache.StaticResourceController;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

	private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

	private final Socket connection;
	private final RequestMapping requestMapping;

	public Http11Processor(final Socket connection) {
		this.connection = connection;
		this.requestMapping = new RequestMapping();
		requestMapping.register(new StaticResourceController());
		requestMapping.register(new HomeController());
		requestMapping.register(new LoginController(SessionManager.getInstance()));
		requestMapping.register(new RegisterController());
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

			var response = requestMapping.getController(request).handle(request);

			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}
}
