package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.controller.Controller;
import org.apache.controller.RootController;
import org.apache.controller.StaticFileController;
import org.apache.coyote.Processor;
import org.apache.exception.HttpMessageParsingException;
import org.apache.exception.ResourceNotFound;
import org.apache.http.HttpRequestMessage;
import org.apache.http.HttpResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final List<Controller> controllers = List.of(new RootController());
    private static final StaticFileController staticFileController = new StaticFileController();

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

            HttpRequestMessage request = makeRequest(inputStream);
            HttpResponseMessage response = makeResponse(outputStream);

            Controller controller = findControllerByRequest(request);
            if (controller != null) {
                //TODO: 컨트롤러가 요청을 처리하다가 예외가 발생할 경우 예외 응답 처리  (2025-09-4, 목, 12:56)
                controller.processRequest(request, response);
                response.writeMessage();
            }

            try {
                staticFileController.processResourceRequest(request, response);
                response.writeMessage();
            } catch (ResourceNotFound e) {
                //TODO: URL이 올바르지 않다는 예외 응답 처리  (2025-09-4, 목, 17:7)
                throw new IllegalArgumentException("URI가 올바르지 않습니다.");
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequestMessage makeRequest(InputStream inputStream) {
        try {
            //TODO: 요청이 연속으로 와서 다음 메세지의 일부까지 읽어버릴 경우 대처  (2025-09-4, 목, 18:21)
            List<String> message = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                message.add(line);
            }

            return new HttpRequestMessage(message);
        } catch (IOException | IllegalArgumentException e) {
            throw new HttpMessageParsingException("HTTP 요청 메세지가 올바르지 않습니다.");
        }
    }

    private HttpResponseMessage makeResponse(OutputStream outputStream) {
        return new HttpResponseMessage(outputStream);
    }

    private Controller findControllerByRequest(HttpRequestMessage request) {
        for (Controller controller : controllers) {
            if (controller.isProcessableRequest(request)) {
                return controller;
            }
        }
        return null;
    }
}
