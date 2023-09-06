package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.RequestHandlerMapper;
import org.apache.coyote.http11.header.HeaderName;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final RequestHandlerMapper requestHandlerMapper = new RequestHandlerMapper();
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int DEFAULT_BUFFER_SIZE = 1024;

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
        try (
            final InputStream inputStream = connection.getInputStream();
            final InputStreamReader inputStreamReader =
                new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final OutputStream outputStream = connection.getOutputStream()
        ) {
            final HttpRequest request = createHttpRequest(bufferedReader);
            final HttpResponse response = requestHandlerMapper.getHandler(request).handle(request);

            outputStream.write(response.convertToMessage().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final String start = bufferedReader.readLine();

        final List<String> headerLines = new ArrayList<>();
        String headerLine = bufferedReader.readLine();
        while (Objects.nonNull(headerLine) && !headerLine.isEmpty()) {
            headerLines.add(headerLine);
            headerLine = bufferedReader.readLine();
        }
        final Headers headers = new Headers(headerLines);

        final String contentLengthHeader = "content-length";
        String body = "";

        if (headers.containsHeader(HeaderName.CONTENT_LENGTH.toString())) {
            final String contentLengthValue = headers.getHeaderValue(contentLengthHeader);
            final int bodyLength = Integer.parseInt(contentLengthValue.strip());

            final StringBuilder bodyBuilder = new StringBuilder();

            int totalRead = 0;
            final char[] buffer = new char[DEFAULT_BUFFER_SIZE];

            while (totalRead < bodyLength) {
                final int readBytesCount =
                    bufferedReader.read(buffer, 0, Math.min(buffer.length, bodyLength - totalRead));
                if (readBytesCount == -1) {
                    break;
                }
                bodyBuilder.append(buffer, 0, readBytesCount);
                totalRead += readBytesCount;
            }

            body = bodyBuilder.toString();
        }

        return new HttpRequest(start, headers, body);
    }
}
