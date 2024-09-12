package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.handler.HandlerMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.view.ViewResolver;
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
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e, this);
        }
    }

    private String getResponse(HttpRequest request) throws IOException {
        Controller controller = handlerMapping.getController(request.header());
        if (controller != null) {
            HttpResponse httpResponse = controller.process(request);
            return httpResponse.toString();
        }

        return viewResolver.resolve(request.header());
    }
}
