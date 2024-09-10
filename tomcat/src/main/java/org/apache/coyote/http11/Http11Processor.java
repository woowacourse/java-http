package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.Handler;
import org.apache.coyote.controller.RequestMapping;
import org.apache.coyote.controller.StaticResourceHandler;
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
             final var requestReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final var request = RequestGenerator.accept(requestReader);
            final var response = HttpResponse.create();
            log.info("request: {}", request);
            service(request, response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void service(HttpRequest request, HttpResponse response) throws IOException {
        Handler handler = RequestMapping.findHandler(request);
        if (handler != null) {
            handler.handle(request, response);
        }
        // todo: GET 메서드가 아닌 경우 405 Method Not Allowed 응답을 반환
        StaticResourceHandler.getInstance().handle(request, response);
    }
}
