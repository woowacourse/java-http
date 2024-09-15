package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.ServletContainer;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.converter.HttpRequestConverter;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ServletContainer servletContainer;

    public Http11Processor(final Socket connection, ServletContainer container) {
        this.connection = connection;
        this.servletContainer = container;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            HttpRequest httpRequest = HttpRequestConverter.convertFrom(bufferedReader);
            HttpResponse httpResponse = new HttpResponse(outputStream);

            servletContainer.invoke(httpRequest, httpResponse);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
