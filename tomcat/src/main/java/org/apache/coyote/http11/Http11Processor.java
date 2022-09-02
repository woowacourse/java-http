package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.LoginHandler;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.enums.ContentType;
import org.apache.coyote.http11.enums.HttpStatus;
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
        try (final BufferedReader bufferedReader = convert(connection.getInputStream());
             final OutputStream outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = new HttpRequest(extractURI(bufferedReader.readLine()));
            final HttpResponse httpResponse = process(httpRequest);

            outputStream.write(httpResponse.generateResponse()
                    .getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private BufferedReader convert(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private HttpResponse process(final HttpRequest httpRequest) {
        if (httpRequest.isFindFile()) {
            ContentType contentType = httpRequest.findContentType();
            String responseBody = httpRequest.findFilePath()
                    .generateFile();
            return new HttpResponse(HttpStatus.OK, contentType, responseBody);
        }

        LoginHandler loginHandler = new LoginHandler();
        return loginHandler.login(httpRequest);
    }

    private String extractURI(final String text) {
        return text.split(BLANK)[URI_INDEX];
    }
}
