package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.handler.ErrorHandler;
import org.apache.coyote.mapper.RequestMapping;
import org.apache.coyote.mapper.ViewMapping;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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

            HttpRequest request = HttpRequest.from(inputStream);
            log.info("http request : {}", request);
            HttpResponse response = HttpResponse.from(request);

            handleRequest(request, response);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException exception) {
            log.error("요청 처리 과정 중 에러 발생 : {}", exception.getMessage());
        }
    }

    private void handleRequest(HttpRequest request, HttpResponse response) {
        try {
            RequestMapping requestMapping = new RequestMapping();
            Controller controller = requestMapping.getController(request);
            if (controller != null) {
                controller.service(request, response);
            } else {
                ViewMapping viewMapping = new ViewMapping();
                View view = viewMapping.resolveView(request.getTargetPath());
                view.render(response);
            }
        } catch (Exception exception) {
            ErrorHandler errorHandler = new ErrorHandler();
            errorHandler.handle(response, exception);
        }
    }
}
