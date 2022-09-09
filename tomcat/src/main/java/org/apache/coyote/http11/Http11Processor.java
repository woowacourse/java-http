package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Optional;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.catalina.servlet.Servlet;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.util.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String NOT_FOUND_PAGE = "404";

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestMapping = new RequestMapping();
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

            final var httpRequest = HttpRequest.from(bufferedReader);
            final var httpResponse = new HttpResponse();

            System.out.println(httpRequest.getRequestURI().getRequestURI());

            // 지원하는 서블릿을 찾는다.
            final Optional<Servlet> servlet = requestMapping.getServlet(httpRequest.getRequestURI());

            // 지원하는 서블릿이 없는 경우 404페이지로 리다이렉트
            servlet.ifPresentOrElse(value -> value.service(httpRequest, httpResponse), () -> {
                httpResponse.setStatusCode(HttpStatus.FOUND);
                httpResponse.setLocation(NOT_FOUND_PAGE);
            });

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
