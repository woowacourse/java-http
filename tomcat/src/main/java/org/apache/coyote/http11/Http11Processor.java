package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.response.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

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

			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String requestLine = reader.readLine();
			String requestUrl = requestLine.split(" ")[1];

			ResponseBody responseBody = ResourceLoader.getInstance().loadResource(requestUrl);

			final var response = String.join("\r\n",
				"HTTP/1.1 200 OK ",
				"Content-Type: " + responseBody.getContentType() + ";charset=utf-8" + " ",
				"Content-Length: " + responseBody.getContentLength() + " ",
				"",
				new String(responseBody.getContent())
			);

			// 바디(바이너리 또는 텍스트 데이터)를 전송
			outputStream.write(response.getBytes());
			outputStream.flush();

		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}
}
