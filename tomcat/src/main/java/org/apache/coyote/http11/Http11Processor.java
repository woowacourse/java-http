package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpStatusCode.BAD_REQUEST;
import static org.apache.coyote.http11.HttpStatusCode.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.http11.HttpStatusCode.METHOD_NOT_ALLOWED;
import static org.apache.coyote.http11.HttpStatusCode.NOT_FOUND;
import static org.apache.coyote.http11.HttpStatusCode.UNAUTHORIZED;
import static util.FileLoader.loadFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.badrequest.BadRequestException;
import org.apache.coyote.http11.exception.badrequest.NotExistHeaderException;
import org.apache.coyote.http11.exception.methodnotallowed.MethodNotAllowedException;
import org.apache.coyote.http11.exception.notfound.NotFoundException;
import org.apache.coyote.http11.exception.unauthorized.InvalidSessionException;
import org.apache.coyote.http11.exception.unauthorized.UnAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection, final RequestMapping requestMapping,
                           final SessionManager sessionManager) {
        this.connection = connection;
        this.requestMapping = requestMapping;
        this.sessionManager = sessionManager;
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
            final HttpReader httpReader = new HttpReader(bufferedReader);
            final Session session = getSession(httpReader);
            final HttpRequest httpRequest = new HttpRequest(httpReader, session);
            final HttpResponse httpResponse = new HttpResponse();
            handleRequest(httpRequest, httpResponse);
            sessionManager.add(session);
            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Session getSession(final HttpReader httpReader) {
        try {
            final String sessionId = httpReader.getSessionId();
            return sessionManager.findSession(sessionId);
        } catch (NotExistHeaderException | InvalidSessionException exception) {
            final UUID uuid = UUID.randomUUID();
            return new Session(uuid.toString());
        }
    }

    private void handleRequest(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        try {
            final Controller controller = requestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);
        } catch (Exception exception) {
            handleException(httpResponse, exception);
        }
    }

    private void handleException(final HttpResponse httpResponse, final Exception exception) throws IOException {
        if (exception instanceof BadRequestException) {
            doException(httpResponse, BAD_REQUEST);
            return;
        }
        if (exception instanceof UnAuthorizedException) {
            doException(httpResponse, UNAUTHORIZED);
            return;
        }
        if (exception instanceof NotFoundException) {
            doException(httpResponse, NOT_FOUND);
            return;
        }
        if (exception instanceof MethodNotAllowedException) {
            doException(httpResponse, METHOD_NOT_ALLOWED);
            return;
        }
        doException(httpResponse, INTERNAL_SERVER_ERROR);
    }

    private void doException(final HttpResponse httpResponse, final HttpStatusCode httpStatusCode) throws IOException {
        final HttpResponseBody httpResponseBody = HttpResponseBody.ofFile(
                loadFile("/" + httpStatusCode.getCode() + ".html"));
        httpResponse.statusCode(httpStatusCode)
                .responseBody(httpResponseBody);
    }
}
