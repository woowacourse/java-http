package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.exception.ApplicationException;
import org.apache.catalina.handler.HandlerMapping;
import org.apache.catalina.view.ViewResolver;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.HttpConnectorException;
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
            String response = getResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e, this);
        } catch (HttpConnectorException e) {
            // todo: 500 error page
            log.error(e.getMessage(), e, this);
        } catch (ApplicationException e) {
            // todo: map to proper error page
            log.error(e.getMessage(), e, this);
        }
    }

    private String getResponse(HttpRequest request) throws IOException, ApplicationException {
        AbstractController controller = handlerMapping.getController(request);
        if (controller != null) {
            HttpResponse response = new HttpResponse();
            controller.service(request, response);
            return response.toString();
        }

        return viewResolver.resolve(request);
    }
}
