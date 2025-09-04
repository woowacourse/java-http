package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import org.apache.controller.Controller;
import org.apache.controller.RootController;
import org.apache.coyote.Processor;
import org.apache.http.HttpRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final List<Controller> controllers = List.of(new RootController());

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

            HttpRequestMessage request = new HttpRequestMessage(inputStream);
            Controller controller = findControllerByRequest(request);
            String response = controller.processRequest(request);
            //TODO: 요청을 처리 가능한 컨트롤러가 없을 경우 예외 응답 처리  (2025-09-4, 목, 12:55)
            //TODO: 컨트롤러가 요청을 처리하다가 예외가 발생할 경우 예외 응답 처리  (2025-09-4, 목, 12:56)

            outputStream.write(response.getBytes());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Controller findControllerByRequest(HttpRequestMessage request) {
        for (Controller controller : controllers) {
            if (controller.isProcessableRequest(request)) {
                return controller;
            }
        }
        throw new IllegalArgumentException("해당 요청을 처리 가능한 컨트롤러가 존재하지 않습니다");
    }


}
