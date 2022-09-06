package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.controller.FileReader;
import nextstep.jwp.controller.LoginController;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_REQUEST_BODY = "Hello world!";
    private static final String CONTENT_LENGTH = "Content-Length";

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
        try (final BufferedReader bufferedReader
                     = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = makeHttpRequest(bufferedReader);

            HttpResponse httpResponse = new HttpResponse();
            doService(httpRequest, httpResponse);

            outputStream.write(httpResponse.getValue().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest makeHttpRequest(final BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.of(bufferedReader.readLine());
        Headers requestHeaders = Headers.of(readRequestHeaders(bufferedReader));
        String body = readRequestBody(bufferedReader, requestHeaders);
        return new HttpRequest(requestLine, requestHeaders, body);
    }

    private List<String> readRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> request = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).isBlank()) {
            request.add(line);
        }
        return request;
    }

    private String readRequestBody(final BufferedReader bufferedReader, final Headers requestHeaders) throws IOException {
        if (requestHeaders.contains(CONTENT_LENGTH)){
            int contentLength = Integer.parseInt(requestHeaders.get(CONTENT_LENGTH));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    private void doService(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        RequestUri requestUri = httpRequest.getRequestUri();
        String resourcePath = requestUri.getResourcePath();
        if (resourcePath.equals("/")) {
            httpResponse
                    .httpStatus(HttpStatus.OK)
                    .body(DEFAULT_REQUEST_BODY, MediaType.HTML);
            return;
        }
        if (resourcePath.startsWith("/login")) {
            new LoginController().service(httpRequest, httpResponse);
            return;
        }
        httpResponse
                    .httpStatus(HttpStatus.OK)
                    .body(FileReader.read(requestUri.getResourcePath()), requestUri.findMediaType());
    }
}
