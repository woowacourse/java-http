package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.coyote.Processor;
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

			final Servlet servlet = new Servlet(reader);
			if (!CookieFilter.doFilter(servlet.getRequest(), servlet.getResponse())) {
				servlet.service();
			}

			outputStream.write(servlet.getResponse().generateHttpResponse().getBytes(StandardCharsets.UTF_8));
			outputStream.flush();

		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}

