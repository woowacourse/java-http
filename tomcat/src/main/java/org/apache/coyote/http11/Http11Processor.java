package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.controller.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.utils.PathFinder;
import org.apache.coyote.http11.utils.RequestMappingHandler;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final HttpRequest httpRequest = HttpRequest.parse(bufferedReader);
            final HttpResponse response = makeResponse(httpRequest);
            outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse makeResponse(final HttpRequest httpRequest) throws Exception {
        try {
            final Controller controller = RequestMappingHandler.findResponseMaker(httpRequest);
            return controller.service(httpRequest);
        } catch (final IllegalArgumentException e) {
            final Path path = PathFinder.findPath("/400.html");
            final String responseBody = new String(Files.readAllBytes(path));
            return new HttpResponse(HttpStatus.FOUND, responseBody, ContentType.HTML, "/400.html");
        }
    }
}
