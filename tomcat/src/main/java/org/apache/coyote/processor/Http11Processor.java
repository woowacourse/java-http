package org.apache.coyote.processor;

import java.io.IOException;
import java.net.Socket;
import org.apache.catalina.ServletContainer;
import org.apache.catalina.exception.UncheckedServletException;
import org.apache.catalina.http.HeaderName;
import org.apache.catalina.http.StatusCode;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.coyote.connector.RequestReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ServletContainer servletContainer;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.servletContainer = new ServletContainer(new ControllerMapper());
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

            HttpRequest httpRequest = RequestReader.readRequest(inputStream);
            HttpResponse httpResponse = new HttpResponse();

            if (httpRequest.isStaticRequest()) {
                httpResponse.addHeader(HeaderName.CONTENT_TYPE, httpRequest.getContentType());
                httpResponse.setStatusCode(StatusCode.OK);
                httpResponse.setBody(httpRequest.getPath(), httpRequest.getContentType());
            }
            if (!httpRequest.isStaticRequest()) {
                servletContainer.run(httpRequest, httpResponse);
            }

            outputStream.write(httpResponse.getReponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
