package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.NotFoundHandler;
import org.apache.coyote.http11.handler.RegisterHandler;
import org.apache.coyote.http11.handler.StaticResourceHandler;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final List<Handler> handlers = List.of(
            new LoginHandler(),
            new StaticResourceHandler(),
            new RegisterHandler(),
            new NotFoundHandler()
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
        try (final BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             final OutputStream outputStream = connection.getOutputStream()
        ) {
            final HttpRequest request = HttpRequest.from(reader);

            HttpResponse httpResponse = null;
            for (Handler handler : handlers) {
                if (handler.canHandle(request)) {
                    httpResponse = handler.handle(request);
                    break;
                }
            }

            if (httpResponse != null) {
                outputStream.write(httpResponse.toString().getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
