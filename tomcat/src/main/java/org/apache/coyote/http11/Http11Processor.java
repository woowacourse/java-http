package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.controller.Controller;
import org.apache.controller.ControllerContainer;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.exception.HandlerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String NOT_FOUND_PAGE = "/404.html";
    private static final String SERVER_ERR_PAGE = "/500.html";

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            Http11Request request = Http11Request.from(inputStream);
            log.info("http request : {}", request);

            Http11Response response = createResponse(request);
            sendResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Http11Response createResponse(Http11Request request) throws IOException {
        Http11Response response = Http11Response.create();
        handle(request, response);
        return response;
    }

    private void handle(Http11Request request, Http11Response response) {
        try {
            Controller handler = findHandler(request);
            handler.service(request, response);
        } catch (HandlerNotFoundException e) {
            response.sendRedirect(NOT_FOUND_PAGE);
        } catch (Exception e) {
            response.sendRedirect(SERVER_ERR_PAGE);
        }
    }

    private Controller findHandler(Http11Request request) {
        return ControllerContainer.getHandlers().stream()
                .filter(controller -> controller.isMatch(request.getStartLine()))
                .findFirst()
                .orElseThrow(() -> new HandlerNotFoundException("존재하지 않는 핸들러입니다."));
    }

    private void sendResponse(OutputStream outputStream, Http11Response response) throws IOException {
        outputStream.write(response.toString().getBytes());
        outputStream.flush();
    }
}
