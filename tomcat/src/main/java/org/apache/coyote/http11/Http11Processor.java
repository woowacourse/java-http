package org.apache.coyote.http11;

import java.net.Socket;
import com.techcourse.controller.Controller;
import com.techcourse.controller.FrontController;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final FrontController FRONT_CONTROLLER = new FrontController();

    private final Socket connection;

    /**
     * 단일 요청 처리 진입점
     *
     * @param connection
     */
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

            final HttpRequest request = new HttpRequest(inputStream);
            final HttpResponse response = new HttpResponse(outputStream);
            response.setProtocolVersion(request.getProtocolVersion());
            final Controller controller = FRONT_CONTROLLER.handle(request);
            controller.service(request, response);

            response.setAppendTrailingSpaces(response.getStatusCode() == 200);
            response.send();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
