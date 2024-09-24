package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;

import jakarta.servlet.RequestMapping;
import com.techcourse.servlet.HomeController;
import com.techcourse.servlet.LoginController;
import com.techcourse.servlet.RegisterController;
import org.apache.catalina.servlets.StaticResourceController;
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
			HttpResponse response = HttpResponse.empty();

			if (!request.isHttp11VersionRequest()) {
				throw new IOException("not http1.1 request");
			}

			requestMapping.getController(request).service(request, response);

			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
