package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var request = HttpRequest.from(inputStream);

            final var response = makeResponse(request);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse makeResponse(final HttpRequest httpRequest) throws IOException {
        final var controller = new Controller();
        if (httpRequest.getMethod().equals(HttpMethod.GET)) {
            return doGet(controller, httpRequest);
        }
        if (httpRequest.getMethod().equals(HttpMethod.POST)) {
            return doPost(controller, httpRequest);
        }
        throw new UncheckedServletException("지원하지 않는 Http Method 입니다.");
    }

    private HttpResponse doGet(final Controller controller, final HttpRequest httpRequest) {
        final var path = httpRequest.getPath();
        if (path.equals("/")) {
            return controller.showIndex();
        }
        if (path.equals("/login") && httpRequest.getQueryString() != null) {
            return controller.login(httpRequest.parseQueryString());
        }
        if (path.equals("/login")) {
            return controller.showLogin();
        }
        if (path.equals("/register")) {
            return controller.showRegister();
        }
        return controller.show(path);
    }

    private HttpResponse doPost(final Controller controller, final HttpRequest httpRequest) {
        final var path = httpRequest.getPath();
        if (path.equals("/register")) {
            return controller.register(httpRequest.parseBodyQueryString());
        }
        throw new NotFoundException("존재하지 않는 요청입니다.");
    }
}
