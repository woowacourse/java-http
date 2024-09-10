package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http.Dispatcher;
import org.apache.coyote.http.HttpMessageGenerator;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            Dispatcher dispatcher = new Dispatcher();

            HttpRequest request = HttpMessageGenerator.generateRequest(bufferedReader);
            log.info("\nrequest uri: {}\nrequest method: {}\nrequest body: {}",
                    request.getPath().getUri(), request.getMethod().getMethod(), request.getBody());

            HttpResponse response = new HttpResponse();

            dispatcher.dispatch(request, response);
            log.info("\nresponse: {}", response);

            outputStream.write(response.toResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }
}
