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
import org.apache.exception.InvalidRequestException;
import org.apache.exception.ReqeustMessageParsingException;
import org.apache.exception.RequestProcessingException;
import org.apache.exception.SocketWriteException;
import org.apache.http.HttpRequestMessage;
import org.apache.http.HttpResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final List<Controller> controllers = List.of(
            new RootController(),
            new StaticFileController()
    );

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
            controller.processRequest(request, response);
            response.writeMessage();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (ReqeustMessageParsingException | InvalidRequestException e) {
            //TODO: 400 예외응답을 구성해보자.  (2025-09-5, 금, 1:34)
        } catch (RequestProcessingException | SocketWriteException e) {
            //TODO: 500 예외응답을 구성해보자.  (2025-09-5, 금, 1:34)
        }
    }

    private HttpRequestMessage makeRequest(InputStream inputStream) {
        try {
            List<String> message = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            //TODO: 요청 메세지의 다른 줄도 읽어보자.  (2025-09-5, 금, 1:7)
            message.add(bufferedReader.readLine());
            return new HttpRequestMessage(message);
        } catch (IOException | IllegalArgumentException e) {
            throw new ReqeustMessageParsingException("HTTP 요청 메세지가 올바르지 않습니다.");
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
        throw new InvalidRequestException("URI가 올바르지 않습니다.");
    }
}
