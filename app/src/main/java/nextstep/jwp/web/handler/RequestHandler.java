package nextstep.jwp.web.handler;

import static nextstep.jwp.web.http.response.HttpStatus.OK;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import nextstep.jwp.web.exception.ApplicationRuntimeException;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.ContentType;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.HttpResponseImpl;
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
            List.of(new WebStatusHandler(), new SessionHandler())
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

            HttpRequest request = HttpRequest.of(inputStream);

            HttpResponse response =
                new HttpResponseImpl
                    .Builder(request, OK)
                    .build();

            doService(request, response);
            doHandle(request, response);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void doService(HttpRequest request, HttpResponse response) {
        if (ContentType.isStaticResource(request.methodUrl().url())) {
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
