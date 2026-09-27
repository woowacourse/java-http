package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.RequestMapping;
import com.techcourse.controller.ViewResolver;
import com.techcourse.controller.ViewResolver.View;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
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

            String response = response(inputStream);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String response(InputStream inputStream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final HttpRequest request = new HttpRequest(reader);
        final ViewResolver resolver = ViewResolver.getINSTANCE();

        View view = resolver.resolve(request.getPath());
        final HttpResponse response = new HttpResponse(request.getProtocolVersion());
        response.setBody(view.body());
        response.addHeader("Content-Type", view.contentType());
        final RequestMapping requestMapping = RequestMapping.getInstance();
        final Controller controller = requestMapping.getController(request);

        controller.service(request, response);

        return response.response();
    }
}
