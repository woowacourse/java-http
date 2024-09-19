package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.constants.ContentType;
import org.apache.coyote.http11.constants.HttpHeader;
import org.apache.coyote.http11.constants.HttpStatus;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.StaticResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            HttpResponse httpResponse = new HttpResponse(httpRequest.getVersion());

            final Controller controller = RequestMapping.from(httpRequest);
            try {
                controller.service(httpRequest, httpResponse);
            } catch (final Exception e) {
                log.error(e.getMessage());
                final String body = StaticResourceReader.read("/500.html");
                httpResponse.setHttpStatus(HttpStatus.INTERNATIONAL_SERVER_ERROR);
                httpResponse.addHeader(HttpHeader.CONTENT_TYPE, ContentType.HTML.getContentTypeUtf8());
                httpResponse.setBody(body);
            }

            outputStream.write(httpResponse.serializeResponse());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
