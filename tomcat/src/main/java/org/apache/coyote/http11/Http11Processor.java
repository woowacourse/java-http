package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.reqeust.HttpRequest;
import org.apache.coyote.http11.handler.HttpHandler;
import org.apache.coyote.http11.handler.HttpHandlerMapper;
import org.apache.coyote.http11.reqeust.util.HttpRequestBuilder;
import org.apache.coyote.http11.reqeust.util.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()
        ) {
            final HttpRequest request = getHttpRequest(inputStream);
            final HttpHandlerMapper handlerMapper = HttpHandlerMapper.getInstance();
            final HttpHandler handler = handlerMapper.getHandler(request);
            final HttpResponse response = handler.handle(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest getHttpRequest(final InputStream inputStream) throws IOException {
        final HttpRequestParser parser = HttpRequestParser.getInstance();

        final List<String> requestLines = getRequestLines(inputStream);
        final String startLine = requestLines.getFirst();
        final List<String> headerLines = requestLines.subList(1, requestLines.size());

        return new HttpRequestBuilder()
                .method(parser.parseHttpMethod(startLine))
                .uri(parser.parseRequestUri(startLine))
                .protocolVersion(parser.parseHttpVersion(startLine))
                .headers(HttpHeaders.parseHeaders(headerLines))
                .body(getBodyIfExists(inputStream, HttpHeaders.parseHeaders(headerLines)))
                .build();
    }

    private List<String> getRequestLines(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> requestLines = new ArrayList<>();
        while (true) {
            final String line = bufferedReader.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            requestLines.add(line);
        }

        return requestLines;
    }

    private String getBodyIfExists(
            final InputStream inputStream,
            final HttpHeaders headers
    ) throws IOException {
        if (headers.getHeader("Content-Length") == null) {
            return null;
        }
        int contentLength = Integer.parseInt(headers.getHeader("Content-Length"));
        byte[] bodyBytes = inputStream.readNBytes(contentLength);

        return new String(bodyBytes, StandardCharsets.UTF_8);
    }
}
