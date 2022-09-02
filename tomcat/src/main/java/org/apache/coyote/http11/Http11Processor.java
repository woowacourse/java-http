package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.LoginHandler;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String BLANK = " ";
    private static final int URI_INDEX = 1;
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
        try (final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = new HttpRequest(extractURI(bufferedReader.readLine()));
            checkCallApi(httpRequest);

            final String responseBody = httpRequest.findFilePath()
                    .generateFile();
            final String response = generateResponse(httpRequest, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void checkCallApi(final HttpRequest httpRequest) {
        if (httpRequest.isCallApi()) {
            LoginHandler loginHandler = new LoginHandler();
            loginHandler.login(httpRequest.getQueryString());
        }
    }

    private String extractURI(final String text) {
        return text.split(BLANK)[URI_INDEX];
    }

    private String generateResponse(final HttpRequest httpRequest, final String responseBody) throws IOException {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + httpRequest.findContentType().getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
