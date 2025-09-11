package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.RequestMapping;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(
            final Socket connection,
            final RequestMapping requestMapping
    ) {
        this.connection = connection;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        Http11Response response = new Http11Response();
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final var httpRequest = parseRequest(inputStream);
            dispatch(httpRequest, response);
            outputStream.write(response.getResponseBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.setStatus(500);
            response.setBody("Server Error", "text/plain;charset=utf-8");
            try {
                connection.getOutputStream().write(response.getResponseBytes());
                connection.getOutputStream().flush();
            } catch (IOException ioException) {
                log.error(ioException.getMessage(), ioException);
            }
        }
    }

    private Http11Request parseRequest(final InputStream inputStream) throws IOException {
        final String requestLineString = HttpParser.readLine(inputStream);
        if (requestLineString.isBlank()) {
            return Http11Request.createInvalid();
        }
        final var requestLine = RequestLine.from(requestLineString);
        final var headers = HttpHeaders.from(inputStream);
        final String body = parseBody(inputStream, headers);
        return new Http11Request(
                requestLine,
                headers,
                body
        );
    }

    private String parseBody(
            final InputStream inputStream,
            final HttpHeaders headers
    ) throws IOException {
        final int contentLength = headers.getContentLength();
        if (contentLength == 0) {
            return "";
        }
        final var bodyBytes = inputStream.readNBytes(contentLength);
        return new String(bodyBytes, StandardCharsets.UTF_8);
    }

    private void dispatch(
            final Http11Request httpRequest,
            final Http11Response httpResponse
    ) throws Exception {
        final var path = httpRequest.getPath();
        final var controller = requestMapping.getController(path);
        controller.service(httpRequest, httpResponse);
    }
}

