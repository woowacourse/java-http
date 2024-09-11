package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping REQUEST_MAPPING = new RequestMapping();

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
//        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            List<String> requestLines = Http11InputStreamReader.read(inputStream);
            HttpRequest request = HttpRequest.parse(requestLines);
//            log.debug(request.toString());

            HttpResponse.Builder responseBuilder = HttpResponse.builder();
            REQUEST_MAPPING.getController(request)
                    .service(request, responseBuilder);
            HttpResponse response = responseBuilder.build();
//            log.debug(response.toString());

            outputStream.write(response.toMessage());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
