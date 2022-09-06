package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HttpController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Controller controller;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.controller = new HttpController();
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            String requestLine = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            HttpRequest httpRequest = HttpRequest.ofRequestLine(requestLine);
            HttpResponse httpResponse = new HttpResponse();

            String response = generateResponse(httpRequest, httpResponse);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String generateResponse(final HttpRequest httpRequest,
                                    final HttpResponse httpResponse) throws IOException {
        controller.service(httpRequest, httpResponse);

        return String.join("\r\n",
                httpResponse.getStatusLine().getValue(),
                httpRequest.getContentType(),
                "Content-Length: " + httpResponse.getResponseBody().getBytes().length + " ",
                "",
                httpResponse.getResponseBody());
    }
}
