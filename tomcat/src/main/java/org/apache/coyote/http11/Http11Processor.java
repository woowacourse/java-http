package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpHandler httpHandler;

    public Http11Processor(
            final Socket connection,
            final HttpHandler httpHandler
    ) {
        this.connection = connection;
        this.httpHandler = httpHandler;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final var inputStream = new BufferedInputStream(connection.getInputStream());
                final var outputStream = connection.getOutputStream()
        ) {
            final HttpRequest request = readHttpRequest(inputStream);
            final HttpResponse response = new HttpResponse();

            httpHandler.handle(request, response);

            outputStream.write(response.getResponse());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static HttpRequest readHttpRequest(final BufferedInputStream inputStream) throws IOException {
        final RequestLine requestLine = readRequestLine(inputStream);
        final Headers headers = readHeaders(inputStream);
        final RequestBody requestBody = readRequestBody(inputStream, headers.contentLength());

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static RequestLine readRequestLine(final BufferedInputStream inputStream) throws IOException {
        return new RequestLine(readLine(inputStream));
    }

    private static Headers readHeaders(final BufferedInputStream inputStream) throws IOException {
        final Headers headers = new Headers();

        String line = readLine(inputStream);
        while (!"".equals(line)) {
            if (line == null) {
                throw new IllegalArgumentException("헤더가 올바르지 않습니다.");
            }
            headers.add(line);
            line = readLine(inputStream);
        }
        return headers;
    }

    private static RequestBody readRequestBody(
            final BufferedInputStream inputStream,
            final int contentLength
    ) throws IOException {
        final byte[] body = inputStream.readNBytes(contentLength);
        return new RequestBody(new String(body, StandardCharsets.UTF_8));
    }

    private static String readLine(final BufferedInputStream inputStream) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int value;
        while ((value = inputStream.read()) != -1 && value != '\n') {
            buffer.write(value);
        }
        if (value == -1 && buffer.size() == 0) {
            return null;
        }

        final byte[] bytes = buffer.toByteArray();
        final int length = (bytes.length > 0) && (bytes[bytes.length - 1] == '\r')
                ? bytes.length - 1
                : bytes.length;
        return new String(bytes, 0, length, StandardCharsets.UTF_8);
    }
}
