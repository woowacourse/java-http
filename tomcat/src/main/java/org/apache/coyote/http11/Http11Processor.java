package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String ACCEPT_HEADER = "Accept";
    private static final String COOKIE_HEADER = "Cookie";
    private static final String SET_COOKIE_HEADER = "Set-Cookie";

    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String TEXT_HTML = "text/html;charset=utf-8";
    private static final String TEXT_CSS = "text/css";

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, RequestMapping requestMapping) {
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
             final var inputStreamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            final var request = HttpRequest.from(bufferedReader);
            if (request == null) {
                return;
            }

            log.info("{}", request);

            final var cookie = new HttpCookie(request.getHeader(COOKIE_HEADER));
            var setCookie = "";
            if (!cookie.hasJSessionId()) {
                setCookie = HttpCookie.JSESSIONID + "=" + UUID.randomUUID();
            }

            final var response = new HttpResponse();
            response.setHeader(CONTENT_TYPE_HEADER, decideContentType(request.getHeader(ACCEPT_HEADER)));
            if (!setCookie.isEmpty()) {
                response.setHeader(SET_COOKIE_HEADER, setCookie);
            }

            requestMapping.getController(request).service(request, response);

            log.info("{}", response);

            outputStream.write(response.build().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String decideContentType(final String accept) {
        if (accept != null && accept.contains(TEXT_CSS)) {
            return TEXT_CSS;
        }
        return TEXT_HTML;
    }
}
