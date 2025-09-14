package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.presentation.HttpRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final HttpRequest request = parseHttpRequest(bufferedReader);
            if (request == null) {
                return;
            }

            final String response = new RequestProcessor().process(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = parseRequestLine(bufferedReader);
        if (requestLine == null) {
            return null;
        }

        final List<String> headers = parseHeaders(bufferedReader);
        final String body = parseBody(bufferedReader, requestLine, headers);

        return HttpRequest.builder()
                .requestLine(requestLine)
                .headers(headers)
                .params(body)
                .build();
    }

    private RequestLine parseRequestLine(final BufferedReader bufferedReader) throws IOException {
        final String line = bufferedReader.readLine();
        if (line == null) {
            return null;
        }
        return new RequestLine(line);
    }

    private List<String> parseHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> headers = new ArrayList<>();
        String line;
        while (!"".equals(line = bufferedReader.readLine())) {
            headers.add(line);
        }
        return headers;
    }

    private String parseBody(
            final BufferedReader bufferedReader,
            final RequestLine requestLine,
            final List<String> headers
    ) throws IOException {
        final HttpRequest requestForContentLength = HttpRequest.builder()
                .requestLine(requestLine)
                .headers(headers)
                .build();
        final int contentLength = requestForContentLength.getContentLength();

        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
