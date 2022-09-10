package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.apache.catalina.core.controller.ControllerContainer;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ControllerContainer container;

    public Http11Processor(final Socket connection, final ControllerContainer container) {
        this.connection = connection;
        this.container = container;
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

            HttpRequest httpRequest = new HttpRequest(bufferReader);
            HttpResponse httpResponse = new HttpResponse(httpRequest.findContentType());

            container.service(httpRequest, httpResponse);

            outputStream.write(httpResponse.getResponseTemplate().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
