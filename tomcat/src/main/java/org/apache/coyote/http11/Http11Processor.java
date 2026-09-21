package org.apache.coyote.http11;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.pageController.RequestDispatcher;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestReader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Set;

public class Http11Processor implements Runnable, Processor {
    private static final Set<HttpMethod> SUPPORTED_METHODS = Set.of(HttpMethod.GET, HttpMethod.POST);

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestReader requestReader;
    private final RequestDispatcher requestDispatcher;

    public Http11Processor(
            final Socket connection,
            final SessionManager sessionManager,
            final RequestDispatcher requestDispatcher
    ) {
        this.connection = connection;
        this.requestReader = new HttpRequestReader(SUPPORTED_METHODS, sessionManager);
        this.requestDispatcher = requestDispatcher;
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
            HttpResponse response = createResponse(inputStream);

            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createResponse(InputStream inputStream) throws IOException {
        HttpResponse response = new HttpResponse();

        try {
            HttpRequest request = requestReader.read(inputStream);

            requestDispatcher.dispatch(request, response);
            issueSessionCookie(request, response);
        } catch (BadRequestException e) {
            log.warn(e.getMessage());
            response.reset();
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setBody("text/plain", e.getMessage());
        }

        return response;
    }

    private void issueSessionCookie(HttpRequest request, HttpResponse response) {
        if (request.getSession(false) != null) {
            return;
        }

        Session session = request.getSession(true);
        response.addCookie(HttpCookie.JSESSIONID, session.getId());
    }
}
