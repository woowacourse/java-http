package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, final RequestMapping requestMapping) {
        this.connection = connection;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final Controller controller = requestMapping.getController(httpRequest);
            final HttpResponse httpResponse = controller.service(httpRequest);
            final String result = httpResponse.toString();

            outputStream.write(result.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error("비즈니스 오류 : ", e);
        } catch (Exception e) {
            log.error("예상치 못한 오류 : ", e);
        }
    }
}
