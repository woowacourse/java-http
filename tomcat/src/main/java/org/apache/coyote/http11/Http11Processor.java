package org.apache.coyote.http11;

import static org.apache.catalina.controller.HomeController.HOME;
import static org.apache.catalina.controller.IndexController.INDEX;
import static org.apache.catalina.controller.LoginController.LOGIN;
import static org.apache.catalina.controller.RegisterController.RESISTER;
import static org.apache.coyote.http11.response.ResponseHeader.ALLOW;
import static org.apache.coyote.http11.response.StatusCode.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.http11.response.StatusCode.METHOD_NOT_ALLOWED;
import static org.apache.coyote.http11.response.StatusCode.NOT_FOUND;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.HomeController;
import org.apache.catalina.controller.IndexController;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.controller.StaticFileController;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<String, Controller> CONTAINER = new HashMap<>();
    private static final StaticFileController STATIC_FILE_CONTROLLER = new StaticFileController();
    private static final String NOT_FOUND_HTML = "/404.html";
    private static final String METHOD_NOT_ALLOWED_HTML = "/405.html";
    private static final String INTERNAL_SERVER_ERROR_HTML = "/500.html";

    private final Socket connection;

    static {
        CONTAINER.put(INDEX, new IndexController());
        CONTAINER.put(HOME, new HomeController());
        CONTAINER.put(LOGIN, new LoginController());
        CONTAINER.put(RESISTER, new RegisterController());
    }


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

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final Request request = Request.convert(bufferedReader);
            Response responseEntity = getResponse(request);
            final String response = responseEntity.parse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Response getResponse(final Request request) throws IOException {
        try {
            final Controller controller = CONTAINER.getOrDefault(request.getPath(), STATIC_FILE_CONTROLLER);
            return controller.service(request);
        } catch (MethodNotAllowedException exception) {
            final ContentType contentType = ContentType.findByName(METHOD_NOT_ALLOWED_HTML);
            final String responseBody = FileReader.read(METHOD_NOT_ALLOWED_HTML);
            final Response response = Response.of(request.getHttpVersion(), METHOD_NOT_ALLOWED, contentType, responseBody);
            response.setHeader(ALLOW, exception.getAllowedMethods());
            return response;
        } catch (IOException exception) {
            final ContentType contentType = ContentType.findByName(NOT_FOUND_HTML);
            final String responseBody = FileReader.read(NOT_FOUND_HTML);
            return Response.of(request.getHttpVersion(), NOT_FOUND, contentType, responseBody);
        } catch (Exception runtimeException) {
            log.error(runtimeException.getMessage(), runtimeException);
            final ContentType contentType = ContentType.findByName(INTERNAL_SERVER_ERROR_HTML);
            final String responseBody = FileReader.read(INTERNAL_SERVER_ERROR_HTML);
            return Response.of(request.getHttpVersion(), INTERNAL_SERVER_ERROR, contentType, responseBody);
        }
    }
}
