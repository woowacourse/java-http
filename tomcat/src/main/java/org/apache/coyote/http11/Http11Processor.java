package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.controller.Controller;
import org.apache.controller.LoginController;
import org.apache.controller.RootController;
import org.apache.controller.StaticFileController;
import org.apache.coyote.Processor;
import org.apache.exception.DataNotFoundException;
import org.apache.exception.InvalidRequestException;
import org.apache.exception.SockerReadException;
import org.apache.exception.SocketWriteException;
import org.apache.http.HttpRequestMessage;
import org.apache.http.HttpResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final List<Controller> controllers = List.of(
            new LoginController(),
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
            HttpResponseMessage response = makeResponse();

            Controller controller = findControllerByRequest(request);
            controller.processRequest(request, response);

            writeResponseMessage(response, outputStream);

        } catch (InvalidRequestException e) {
            //TODO: 400 예외응답을 구성해보자.  (2025-09-5, 금, 1:34)
        } catch (DataNotFoundException exception) {
            //TODO: 404 예외응답을 구성해보자.  (2025-09-5, 금, 16:34)
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //TODO: 500 예외응답을 구성해보자.  (2025-09-5, 금, 1:34)
        }
    }

    private HttpRequestMessage makeRequest(InputStream inputStream) {
        List<String> message = readRequestMessage(inputStream);
        return new HttpRequestMessage(message);
    }

    private HttpResponseMessage makeResponse() {
        return new HttpResponseMessage();
    }

    private List<String> readRequestMessage(InputStream inputStream) {
        try {
            //TODO: 요청 메세지의 다른 줄도 읽어보자.  (2025-09-5, 금, 1:7)
            List<String> message = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            message.add(bufferedReader.readLine());
            return message;
        } catch (IOException e) {
            throw new SockerReadException("HTTP 요청 메세지가 올바르지 않습니다.");
        }
    }

    private void writeResponseMessage(HttpResponseMessage response, OutputStream outputStream) {
        String message = response.getMessage();
        try {
            outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            throw new SocketWriteException("소켓에 데이터를 쓰는중 오류가 발생했습니다.");
        }
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
