package org.apache.coyote.http11;

import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.Controller;
import org.apache.coyote.http11.handler.HttpControllerMapper;
import org.apache.coyote.http11.handler.HttpHandlerMapperFactory;
import org.apache.coyote.http11.handler.StaticResourceHttpHandler;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpRequestReader;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
            HttpControllerMapper httpControllerMapper = HttpHandlerMapperFactory.create();
            StaticResourceHttpHandler staticResourceHttpHandler = new StaticResourceHttpHandler();

            HttpRequestReader reader = new HttpRequestReader(inputStream);
            HttpResponseWriter writer = new HttpResponseWriter(outputStream);
            HttpRequest request = reader.read();
            Controller controller = httpControllerMapper.findController(request)
                    .orElse(staticResourceHttpHandler);
            HttpResponse response = new HttpResponse();
            controller.service(request, response);
            writer.write(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
