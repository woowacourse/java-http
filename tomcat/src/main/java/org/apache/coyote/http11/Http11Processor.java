package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.servlet.ChicChocServlet;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.NotFoundResourceException;
import org.apache.coyote.http11.util.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ChicChocServlet chicChocServlet;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.chicChocServlet = new ChicChocServlet();
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final var http11request = Http11Request.from(bufferedReader);
            final var http11response = new Http11Response();

            // 정적 파일 요청일 경우
            if (http11request.isStaticResource()) {
                final RequestLine requestLine = http11request.getRequestLine();
                final String requestURI = requestLine.getRequestURI().getRequestURI();
                http11response.setResourceURI(requestURI);

                try {
                    http11response.getBytes();
                    http11response.setStatusCode(HttpStatus.OK);
                } catch (NotFoundResourceException e) {
                    http11response.setStatusCode(HttpStatus.NOT_FOUND);
                    http11response.setResourceURI("/404.html");
                } finally {
                    outputStream.write(http11response.getBytes());
                    outputStream.flush();
                }
                return;
            }

            // servlet 요청 처리
            chicChocServlet.doService(http11request, http11response);
            outputStream.write(http11response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
