package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String EMPTY_LINE = "";

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest httpRequest = createHttpRequest(bufferedReader);
            final HttpResponse httpResponse = createHttpResponse(httpRequest);

            outputStream.write(httpResponse.toResponse());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        List<String> requestHeader = new ArrayList<>();
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            addRequestHeader(requestHeader, line);
        }
        return new HttpRequest(requestLine, requestHeader);
    }

    private static void addRequestHeader(List<String> requestHeader, String line) {
        if (!line.equals(EMPTY_LINE)) {
            requestHeader.add(line);
        }
    }

    private HttpResponse createHttpResponse(HttpRequest httpRequest) throws IOException {
        if (FileExtension.hasFileExtension(httpRequest.getUri())) {
            return FrontController.staticFileRequest(httpRequest);
        }
        return FrontController.nonStaticFileRequest(httpRequest);
    }
}
