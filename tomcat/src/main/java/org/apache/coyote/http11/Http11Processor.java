package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.handler.LoginHandler;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.enums.HttpStatus;
import org.apache.coyote.http11.utils.FileUtil;
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
        final String url = httpRequest.getUrl();

        if ("/".equals(url)) {
            return new HttpResponse(HttpStatus.OK, "text/plain", "Hello world!");
        }

        if ("/login".equals(url)) {
            return new LoginHandler().login(httpRequest);
        }

        final File file = FileUtil.findFile(url);
        final String contentType = FileUtil.findContentType(file);
        final String responseBody = FileUtil.generateFile(file);
        return new HttpResponse(HttpStatus.OK, contentType, responseBody);
    }

    private String extractURI(final String startLine) {
        return startLine.split(BLANK)[URI_INDEX];
    }
}
