package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.request.FormUrlEncodedHttpRequestParser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestMapping = RequestMapping.getInstance();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {
            handleRequest(reader, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRequest(final BufferedReader reader, final OutputStream outputStream) throws IOException {
        final HttpResponse response = HttpResponse.createEmptyResponse();
        try {
            final HttpRequest request = HttpRequest.of(new FormUrlEncodedHttpRequestParser(reader));
            final Controller controller = requestMapping.getController(request);
            controller.service(request, response);
        } catch (Exception e) {
            HttpResponseConfigurator.notFound(response);
        }
        sendHttpResponse(response, outputStream);
    }

    private void sendHttpResponse(final HttpResponse response, final OutputStream outputStream) throws IOException {
        final String parsedResponse = response.toResponseText();
        outputStream.write(parsedResponse.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
