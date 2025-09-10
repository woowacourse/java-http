package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.exception.DataNotFoundException;
import org.apache.coyote.exception.InvalidRequestException;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping requestMapping = new RequestMapping();

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

            Controller controller = requestMapping.getController(request);
            controller.service(request, response);

            response.writeMessage(outputStream);

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

//    private void processCommonRequest(HttpRequest request, HttpResponse response) {
//        Optional<Controller> controllerOptional = controllers.stream()
//                .filter(controller -> controller.isProcessableRequest(request))
//                .findFirst();
//        controllerOptional.ifPresent(controller -> controller.processRequest(request, response));
//    }
//
//    private void processResourceLoadRequest(HttpRequest request, HttpResponse response) {
//        boolean isNotProcessedRequest = !response.isProcessed();
//        boolean canProcess = staticFileController.isProcessableRequest(request);
//        if (isNotProcessedRequest && canProcess) {
//            staticFileController.processRequest(request, response);
//        }
//    }
//
//    private void validateRequestProcess(HttpResponse response) {
//        if (!response.isProcessed()) {
//            throw new DataNotFoundException("URI에 해당하는 요청 처리가 존재하지 않습니다.");
//        }
//    }
}
