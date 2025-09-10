package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.io.Http11InputBuffer;
import org.apache.coyote.http11.io.Http11OutputBuffer;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String JAVA_SESSION_ID_KEY = "JSESSIONID";

    private static final Charset DEFAULT_BODY_CHARSET = StandardCharsets.UTF_8;
    private static final Charset DEFAULT_HEADER_CHARSET = StandardCharsets.ISO_8859_1;

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(Socket connection, RequestMapping requestMapping) {
        this.connection = connection;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();) {

            Http11InputBuffer http11InputBuffer = new Http11InputBuffer(inputStream, DEFAULT_HEADER_CHARSET,
                    DEFAULT_BODY_CHARSET);
            Http11OutputBuffer http11OutputBuffer = new Http11OutputBuffer(outputStream);

            HttpRequest httpRequest = http11InputBuffer.read();
            HttpResponse httpResponse = new HttpResponse();

            Controller controller = requestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);

            http11OutputBuffer.write(httpResponse);

        } catch (IOException |
                 UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
