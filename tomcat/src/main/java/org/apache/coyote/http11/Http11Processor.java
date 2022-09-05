package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpRequestParser;
import org.apache.coyote.http11.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            HttpRequest request = HttpRequestParser.from(inputStream).toHttpRequest();

            Controller controller = RequestMapping.getController(request.getPath());
            HttpResponse httpResponse = controller.service(request);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
