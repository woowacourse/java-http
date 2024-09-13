package org.apache.coyote.http11;

import com.techcourse.exception.ApplicationException;
import com.techcourse.exception.StaticResourceNotFoundException;
import com.techcourse.view.ResponseBuilder;
import com.techcourse.view.ViewResolver;
import java.io.IOException;
import java.net.Socket;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.HandlerMapping;
import org.apache.catalina.exception.TomcatException;
import org.apache.coyote.Processor;
import org.apache.coyote.exception.HttpConnectorException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    public static final String HTTP_VERSION = "HTTP/1.1";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestReceiver httpRequestReceiver = new HttpRequestReceiver();
    private final HandlerMapping handlerMapping = new HandlerMapping();
    private final ResponseBuilder responseBuilder = new ResponseBuilder();
    private final ViewResolver viewResolver = new ViewResolver();

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

            HttpRequest request = httpRequestReceiver.receiveRequest(inputStream);
            String response = processResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | HttpConnectorException e) {
            log.error(e.getMessage(), e, this);
        }
    }

    private String processResponse(HttpRequest request) throws IOException, ApplicationException {
        try {
            return processApplicationResponse(request).toString();
        } catch (TomcatException e) {
            return responseBuilder.buildExceptionResponse(e);
        }
    }

    private HttpResponse processApplicationResponse(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        HttpResponse staticResourceResponse = viewResolver.resolve(request, new HttpResponse());
        if (staticResourceResponse != null) {
            return staticResourceResponse;
        }

        Controller controller = handlerMapping.getController(request);
        if (controller != null) {
            controller.service(request, response);
            return response;
        }

        throw new StaticResourceNotFoundException();
    }
}
