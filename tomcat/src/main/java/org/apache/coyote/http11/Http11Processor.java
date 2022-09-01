package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.LoginHandler;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.Uri;
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
        try (final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
             final OutputStream outputStream = connection.getOutputStream()) {

            final Uri uri = new Uri(extractURI(bufferedReader.readLine()));
            checkCallApi(uri);

            final String responseBody = uri.findFilePath()
                    .generateFile();
            final String response = generateResponse(uri, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void checkCallApi(final Uri uri) {
        if (uri.isCallApi()) {
            LoginHandler loginHandler = new LoginHandler();
            loginHandler.login(uri.getQueryString());
        }
    }

    private String extractURI(final String text) {
        return text.split(" ")[1];
    }

    private String generateResponse(final Uri uri, final String responseBody) throws IOException {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + uri.findContentType().getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
