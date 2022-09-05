package org.apache.coyote.http11;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.web.RequestMapping;
import org.apache.coyote.web.request.HttpRequest;
import org.apache.coyote.web.request.HttpRequestParser;
import org.apache.coyote.web.response.SimpleHttpResponse;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final BufferedReader bufferedReader =
                     new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             final OutputStream bufferedOutputStream = new BufferedOutputStream(connection.getOutputStream())) {

            HttpRequest httpRequest = HttpRequestParser.parse(bufferedReader);
            SimpleHttpResponse simpleHttpResponse = new SimpleHttpResponse(bufferedOutputStream);
            RequestMapping requestMapping = new RequestMapping();
            requestMapping.handle(httpRequest, simpleHttpResponse);
        } catch (IOException | UncheckedServletException | NullPointerException e) {
            log.error(e.getMessage(), e);
        }
    }
}
