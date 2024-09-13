package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerRegistry;
import org.apache.coyote.Processor;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final HttpRequest request = new HttpRequest(bufferedReader);
            final HttpResponse response = new HttpResponse();
            serviceRequest(request, response);
            sendResponse(response, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void serviceRequest(final HttpRequest request, final HttpResponse response) {
        try {
            final Controller controller = ControllerRegistry.getController(request);
            controller.service(request, response);
        } catch (Exception e) {
            response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR); // TODO: 다른 에러들 핸들링 잘 되는지 확인 필요
        }
    }

    private void sendResponse(final HttpResponse response, final OutputStream outputStream) throws IOException {
        final String serializedResponse = Http11Parser.writeHttpResponse(response);
        outputStream.write(serializedResponse.getBytes());
        outputStream.flush();
    }
}
