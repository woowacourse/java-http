package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.controller.ControllerFinder;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.statusLine.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

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
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))
        ) {
            final HttpResponse response = getHttpResponse(bufferedReader);

            bufferedWriter.write(response.convertToString());
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static HttpResponse getHttpResponse(final BufferedReader bufferedReader) throws IOException {
        try {
            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final ResponseEntity responseEntity = ControllerFinder.find(httpRequest);
            return HttpResponse.of(httpRequest.getHttpVersion(), responseEntity);
        } catch (RuntimeException e) {
            final ResponseEntity responseEntity = ResponseEntity.of(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");
            return HttpResponse.of(HttpVersion.HTTP_1_1, responseEntity);
        }
    }
}
