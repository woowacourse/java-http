package org.apache.coyote.http11;

import static org.apache.common.Config.CHARSET;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
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
        try (final var outputStream = connection.getOutputStream();
             final var br = new BufferedReader(new InputStreamReader(connection.getInputStream(), CHARSET))) {
            final RequestHeader requestHeader = getRequestHeader(br);
            final QueryString queryString = QueryString.from(requestHeader.getOriginRequestURI());
            final RequestBody requestBody = getRequestBody(requestHeader, br);

            final HttpRequest httpRequest = new HttpRequest(requestHeader, requestBody, queryString);
            final RequestHandler handler = new RequestHandler(httpRequest);

            final var responseBody = handler.execute();
            outputStream.write(responseBody.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestBody getRequestBody(final RequestHeader requestHeader, final BufferedReader bufferedReader)
            throws IOException {
        if (requestHeader.hasHeader("Content-Length")) {
            final String contentLength = requestHeader.getHeader("Content-Length");
            final String requestBody = parseRequestBody(contentLength, bufferedReader);
            return RequestBody.from(requestBody);
        }
        return RequestBody.emptyBody();
    }

    private String parseRequestBody(final String contentLength, final BufferedReader bufferedReader)
            throws IOException {
        final int length = Integer.parseInt(contentLength);
        final char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return new String(buffer);
    }

    private RequestHeader getRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeaders = new ArrayList<>();
        String temp;
        while (!Objects.equals(temp = bufferedReader.readLine(), "")) {
            if (temp == null) {
                break;
            }
            requestHeaders.add(temp);
        }
        return RequestHeader.from(requestHeaders);
    }
}
