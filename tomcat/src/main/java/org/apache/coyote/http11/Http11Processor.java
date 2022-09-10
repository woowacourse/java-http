package org.apache.coyote.http11;

import static org.apache.coyote.http11.util.HttpStatus.NOT_FOUND;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.ResourceURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final ResourceURI NOT_FOUND_PAGE = ResourceURI.from("/404.html");

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestMapping = RequestMapping.getRequestMapping();
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            final var httpRequest = HttpRequest.from(bufferedReader);
            final var httpResponse = new HttpResponse();

            // 지원하는 서블릿을 찾는다.
            final var servlet = requestMapping.getServlet(httpRequest.getRequestURI());

            // 지원하는 서블릿이 없는 경우 404페이지 랜더링
            servlet.ifPresentOrElse(it -> it.service(httpRequest, httpResponse), () -> {
                httpResponse.setStatusCode(NOT_FOUND);
                httpResponse.setResourceURI(NOT_FOUND_PAGE);
            });

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
