package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.apache.controller.Controller;
import org.apache.controller.LoginController;
import org.apache.controller.LoginPageController;
import org.apache.controller.RegisterController;
import org.apache.controller.RootController;
import org.apache.controller.StaticFileController;
import org.apache.coyote.Processor;
import org.apache.exception.DataNotFoundException;
import org.apache.exception.InvalidRequestException;
import org.apache.exception.SocketWriteException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final StaticFileController staticFileController = new StaticFileController();
    private static final List<Controller> controllers = List.of(
            new LoginController(),
            new LoginPageController(),
            new RegisterController(),
            new RootController());

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

            HttpRequest request = new HttpRequest(inputStream);
            HttpResponse response = new HttpResponse(request.getVersion());

            processCommonRequest(request, response);
            processResourceLoadRequest(request, response);
            validateRequestProcess(response);

            writeResponseMessage(response, outputStream);

        } catch (InvalidRequestException e) {
            log.info(e.getMessage(), e);
            //TODO: 400 예외응답을 구성해보자.  (2025-09-5, 금, 1:34)
        } catch (DataNotFoundException e) {
            log.info(e.getMessage(), e);
            //TODO: 404 예외응답을 구성해보자.  (2025-09-5, 금, 16:34)
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //TODO: 500 예외응답을 구성해보자.  (2025-09-5, 금, 1:34)
        }
    }

    private void writeResponseMessage(HttpResponse response, OutputStream outputStream) {
        String message = response.getMessage();
        try {
            outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            throw new SocketWriteException("소켓에 데이터를 쓰는중 오류가 발생했습니다.");
        }
    }

    private void processCommonRequest(HttpRequest request, HttpResponse response) {
        Optional<Controller> controllerOptional = controllers.stream()
                .filter(controller -> controller.isProcessableRequest(request))
                .findFirst();
        controllerOptional.ifPresent(controller -> controller.processRequest(request, response));
    }

    private void processResourceLoadRequest(HttpRequest request, HttpResponse response) {
        boolean isNotProcessedRequest = !response.isProcessed();
        boolean canProcess = staticFileController.isProcessableRequest(request);
        if (isNotProcessedRequest && canProcess) {
            staticFileController.processRequest(request, response);
        }
    }

    private void validateRequestProcess(HttpResponse response) {
        if (!response.isProcessed()) {
            throw new DataNotFoundException("URI에 해당하는 요청 처리가 존재하지 않습니다.");
        }
    }
}
