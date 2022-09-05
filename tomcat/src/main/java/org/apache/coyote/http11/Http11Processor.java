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

            List<String> request = readRequest(bufferedReader);
            HttpRequest httpRequest = HttpRequest.of(request);
            HttpResponse httpResponse = new HttpResponse();

            doService(httpRequest, httpResponse);

            outputStream.write(httpResponse.getValue().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readRequest(final BufferedReader bufferedReader) throws IOException {
        final List<String> request = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).isBlank()) {
            request.add(line);
        }
        return request;
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
        if (requestUri.isResourceFileRequest()) {
            httpResponse
                    .httpStatus(HttpStatus.OK)
                    .body(FileReader.read(requestUri.getResourcePath()), requestUri.findMediaType());
        }
    }
}
