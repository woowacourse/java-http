package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;

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
			 final var outputStream = connection.getOutputStream()) {
			var responseBody = "Hello world!";
			final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line = br.readLine();
			if (line == null) {
				return;
			}

			String[] tokens = line.split(" ");
			String url = tokens[1];

			if(url.startsWith("/index.html")){
				final URL resource = getClass().getClassLoader().getResource("static/index.html");
				responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
			}

			final var response = String.join("\r\n",
				"HTTP/1.1 200 OK ",
				"Content-Type: text/html;charset=utf-8 ",
				"Content-Length: " + responseBody.getBytes().length + " ",
				"",
				responseBody);

			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}
}

