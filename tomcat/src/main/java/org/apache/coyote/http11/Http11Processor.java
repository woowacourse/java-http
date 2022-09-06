package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHandler;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOG = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestHandler REQUEST_HANDLER = new RequestHandler();

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
        try (final var inputStreamReader = new InputStreamReader(connection.getInputStream());
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest request = readHttpRequest(bufferedReader);
            final String response = REQUEST_HANDLER.handle(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(final BufferedReader reader) throws IOException {
        final RequestLine requestLine = RequestLine.parse(reader.readLine());
        final RequestHeader header = readHeaders(reader);
        final RequestBody body = readBody(reader);

        return new HttpRequest(requestLine, header, body);
    }

    private RequestHeader readHeaders(final BufferedReader reader) throws IOException {
        final List<String> lines = new ArrayList<>();

        final String endOfHeader = "";
        while (true) {
            final String line = reader.readLine();
            if (endOfHeader.equals(line)) {
                break;
            }
            lines.add(line);
        }
        return RequestHeader.parse(lines);
    }

    private RequestBody readBody(final BufferedReader reader) throws IOException {
        final StringBuilder builder = new StringBuilder();
        while (reader.ready()) {
            final char[] buffer = new char[128];
            final int size = reader.read(buffer);
            builder.append(buffer, 0, size);
        }

        final String body = builder.toString();
        return RequestBody.parse(body);
    }
}
