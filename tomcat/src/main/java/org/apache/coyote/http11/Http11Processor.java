package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.RequestHandler;
import org.apache.coyote.http11.request.RequestHeader;
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

            final RequestHeader requestHeader = readRequestHeader(bufferedReader);
            final String response = REQUEST_HANDLER.handle(requestHeader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private RequestHeader readRequestHeader(final BufferedReader reader) throws IOException {
        final Deque<String> lines = readRequest(reader);

        final String startLine = lines.pollFirst();
        final List<String> headerLines = new ArrayList<>(lines);

        return RequestHeader.parse(startLine, headerLines);
    }

    private Deque<String> readRequest(final BufferedReader reader) throws IOException {
        final Deque<String> lines = new ArrayDeque<>();

        while (true) {
            final String line = reader.readLine();
            if ("".equals(line)) {
                break;
            }
            lines.add(line);
        }
        return lines;
    }
}
