package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.apache.coyote.Processor;
import org.apache.coyote.core.RequestMapping;
import org.apache.coyote.core.controller.Controller;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));

            HttpRequest httpRequest = createHttpRequest(bufferReader);
            HttpResponse httpResponse = createHttpResponseTest();

            Controller controller = new RequestMapping().getController(httpRequest);
            controller.service(httpRequest, httpResponse);

            outputStream.write(httpResponse.getResponseTemplate().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(final BufferedReader bufferReader) throws IOException {
        return new HttpRequestCreator().createHttpRequest(bufferReader);
    }

    private HttpResponse createHttpResponseTest() {
        return new HttpResponse();
    }
}
