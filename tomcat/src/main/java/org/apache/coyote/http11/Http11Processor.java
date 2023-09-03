package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.HandlerMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

	private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

	private static final String END_OF_LINE = "";

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
			 final var outputStream = connection.getOutputStream();
			 final var reader = new BufferedReader(new InputStreamReader(inputStream))) {
			final var requestLine = readRequestLine(reader);
			final var requestHeader = readRequestHeader(reader);
			final var requestBody = readRequestBody(reader, requestHeader);
			final var request = new HttpRequest(requestLine, requestHeader, requestBody);
			final var response = HandlerMapping.handle(request);
			outputStream.write(response.format().getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}

	private HttpRequestLine readRequestLine(final BufferedReader reader) throws IOException {
		return HttpRequestLine.from(reader.readLine());
	}

	private HttpRequestHeader readRequestHeader(final BufferedReader reader) throws IOException {
		final var header = new ArrayList<String>();
		String line;
		while ((!END_OF_LINE.equals(line = reader.readLine()))) {
			header.add(line);
		}
		return HttpRequestHeader.from(header);
	}

	private HttpRequestBody readRequestBody(final BufferedReader reader, final HttpRequestHeader header) throws
		IOException {
		final var contentLength = header.get("Content-Length");
		if (contentLength == null) {
			return HttpRequestBody.empty();
		}
		final var bodySize = Integer.parseInt(contentLength);
		final var buffer = new char[bodySize];
		reader.read(buffer);
		return HttpRequestBody.from(new String(buffer));
	}
}
