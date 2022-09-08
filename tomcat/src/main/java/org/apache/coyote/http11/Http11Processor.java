package org.apache.coyote.http11;

import static org.apache.coyote.request.startline.HttpMethod.GET;
import static org.apache.coyote.request.startline.HttpMethod.POST;
import static org.apache.coyote.response.StatusCode.OK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.handler.RegisterHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.startline.HttpMethod;
import org.apache.coyote.response.ContentType;
import org.apache.coyote.response.HttpResponse;
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
             final var outputStream = connection.getOutputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            final HttpRequest httpRequest = HttpRequest.readRequest(bufferedReader);
            final HttpMethod requestMethod = httpRequest.getRequestMethod();
            String requestUrl = httpRequest.getRequestPath();
            HttpResponse httpResponse = HttpResponse.of(OK, ContentType.from(requestUrl), requestUrl);

            if (requestUrl.contains("login") && requestMethod.equals(GET)) {
                httpResponse = LoginHandler.loginWithGet(httpRequest);
            }
            if (requestUrl.contains("login") && requestMethod.equals(POST)) {
                httpResponse = LoginHandler.login(httpRequest);
            }
            if (requestUrl.contains("register") && requestMethod.equals(POST)) {
                httpResponse = RegisterHandler.register(httpRequest);
            }

            final String response = httpResponse.getResponse();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
