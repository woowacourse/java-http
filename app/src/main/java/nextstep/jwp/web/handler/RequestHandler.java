package nextstep.jwp.web.handler;

import static nextstep.jwp.web.http.response.HttpStatus.OK;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import nextstep.jwp.web.exception.ApplicationRuntimeException;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.mvc.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final RequestMapping requestMapping;
    private final List<WebHandler> webHandlers;

    public RequestHandler(Socket connection) {
        this(connection,
            new RequestMapping(),
            List.of(new WebStatusHandler())
        );
    }

    public RequestHandler(Socket connection, RequestMapping requestMapping,
                          List<WebHandler> webHandlers) {
        this.connection = connection;
        this.requestMapping = requestMapping;
        this.webHandlers = webHandlers;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequest.of(inputStream);

            HttpResponse httpResponse =
                new HttpResponse
                    .Builder(httpRequest.protocol(), OK)
                    .build();

            doService(httpRequest, httpResponse);
            doHandle(httpRequest, httpResponse);

            outputStream.write(httpResponse.toResponseFormat().getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void doService(HttpRequest request, HttpResponse response) {
        if (!request.isJsonOrHtml()) {
            return;
        }

        Controller controller = requestMapping.getController(request.methodUrl());
        try {
            controller.service(request, response);
        } catch (ApplicationRuntimeException e) {
            log.error("In Service Error", e);
            requestMapping.errorController().service(request, response);
        }
    }

    private void doHandle(HttpRequest request, HttpResponse response) {
        webHandlers.forEach(webHandler -> webHandler.doHandle(request, response));
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
