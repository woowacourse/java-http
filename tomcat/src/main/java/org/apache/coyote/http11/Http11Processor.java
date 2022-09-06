package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpHeader.LOCATION;
import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpMethod.POST;
import static org.apache.coyote.http11.HttpStatusCode.BAD_REQUEST;
import static org.apache.coyote.http11.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.HttpStatusCode.INTERNAL_SERVER_ERROR;
import static org.apache.coyote.http11.HttpStatusCode.NOT_FOUND;
import static org.apache.coyote.http11.HttpStatusCode.OK;
import static org.apache.coyote.http11.HttpStatusCode.UNAUTHORIZED;
import static org.apache.coyote.http11.util.FileLoader.loadFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.badrequest.AlreadyRegisterAccountException;
import org.apache.coyote.http11.exception.badrequest.BadRequestException;
import org.apache.coyote.http11.exception.badrequest.NotExistHeaderException;
import org.apache.coyote.http11.exception.notfound.NotFoundException;
import org.apache.coyote.http11.exception.notfound.NotFoundUrlException;
import org.apache.coyote.http11.exception.unauthorised.InvalidSessionException;
import org.apache.coyote.http11.exception.unauthorised.LoginFailException;
import org.apache.coyote.http11.exception.unauthorised.UnAuthorisedException;
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
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpReader httpReader = new HttpReader(bufferedReader);
            final HttpRequest httpRequest = new HttpRequest(httpReader);
            final HttpResponse httpResponse = handleResponse(httpRequest);
            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleResponse(final HttpRequest httpRequest) throws IOException {
        try {
            return getHttpResponse(httpRequest);
        } catch (RuntimeException exception) {
            return handleExceptionResponse(httpRequest, exception);
        }
    }

    private HttpResponse getHttpResponse(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isFileRequest()) {
            final String responseBody = loadFile(httpRequest.getUrl());
            final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
            return getOkHttpResponse(responseBody, httpHeaders);
        }
        if (httpRequest.getUrl().equals("/")) {
            final String responseBody = "Hello world!";
            final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
            return getOkHttpResponse(responseBody, httpHeaders);
        }
        if (httpRequest.getUrl().equals("/login")) {
            final SessionManager sessionManager = new SessionManager();
            if (httpRequest.getHttpMethod() == GET && isLoggedIn(httpRequest, sessionManager)) {
                final HttpHeaders httpHeaders = new HttpHeaders()
                        .addHeader(LOCATION, "/index.html");
                return getRedirectHttpResponse(httpHeaders);
            }
            if (httpRequest.getHttpMethod() == GET && !isLoggedIn(httpRequest, sessionManager)) {
                final String responseBody = loadFile("/login.html");
                final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
                return getOkHttpResponse(responseBody, httpHeaders);
            }
            if (httpRequest.getHttpMethod() == POST) {
                final HttpRequestBody httpRequestBody = httpRequest.getHttpRequestBody();
                final User user = login(httpRequestBody);
                final UUID id = UUID.randomUUID();
                final Session session = new Session(id.toString());
                session.setAttribute("user", user);
                sessionManager.add(session);
                final HttpCookie httpCookie = HttpCookie.ofJSessionId(session.getId());
                final HttpHeaders httpHeaders = new HttpHeaders()
                        .addCookie(httpCookie)
                        .addHeader(LOCATION, "/index.html");
                return getRedirectHttpResponse(httpHeaders);
            }
        }
        if (httpRequest.getUrl().equals("/register")) {
            if (httpRequest.getHttpMethod() == GET) {
                final String responseBody = loadFile("/register.html");
                final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
                return getOkHttpResponse(responseBody, httpHeaders);
            }
            if (httpRequest.getHttpMethod() == POST) {
                final HttpRequestBody httpRequestBody = httpRequest.getHttpRequestBody();
                validateRegistrable(httpRequestBody.getBodyValue("account"));
                registerNewUser(httpRequestBody);
                final HttpHeaders httpHeaders = new HttpHeaders()
                        .addHeader(LOCATION, "/index.html");
                return getRedirectHttpResponse(httpHeaders);
            }
        }
        throw new NotFoundUrlException();
    }

    private boolean isLoggedIn(final HttpRequest httpRequest, final SessionManager sessionManager) {
        try {
            final HttpCookie httpCookie = httpRequest.getHttpCookie();
            sessionManager.findSession(httpCookie.getCookieValue("JSESSIONID"));
            return true;
        } catch (NotExistHeaderException | InvalidSessionException exception) {
            return false;
        }
    }

    private HttpHeaders getHttpHeaders(final HttpRequest httpRequest, final String responseBody) {
        return new HttpHeaders()
                .addHeader(CONTENT_TYPE, getContentType(httpRequest))
                .addHeader(CONTENT_LENGTH, responseBody.getBytes().length);
    }

    private String getContentType(final HttpRequest httpRequest) {
        return "text/" + httpRequest.getFileExtension() + ";charset=utf-8";
    }

    private HttpResponse getOkHttpResponse(final String responseBody, final HttpHeaders httpHeaders) {
        return new HttpResponse.Builder()
                .statusCode(OK)
                .headers(httpHeaders)
                .body(responseBody)
                .build();
    }

    private HttpResponse getRedirectHttpResponse(final HttpHeaders httpHeaders) {
        return new HttpResponse.Builder()
                .statusCode(FOUND)
                .headers(httpHeaders)
                .build();
    }

    private User login(final HttpRequestBody httpRequestBody) {
        final String account = httpRequestBody.getBodyValue("account");
        final String password = httpRequestBody.getBodyValue("password");
        final User user = findRegisteredUser(account);
        if (!user.checkPassword(password)) {
            throw new LoginFailException();
        }
        return user;
    }

    private User findRegisteredUser(final String account) {
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(LoginFailException::new);
        log.info("already register account : {}", account);
        return user;
    }

    private void validateRegistrable(final String account) {
        if (InMemoryUserRepository.existByAccount(account)) {
            throw new AlreadyRegisterAccountException();
        }
    }

    private void registerNewUser(final HttpRequestBody httpRequestBody) {
        final User user = new User(httpRequestBody.getBodyValue("account"),
                httpRequestBody.getBodyValue("password"), httpRequestBody.getBodyValue("email"));
        InMemoryUserRepository.save(user);
    }

    private HttpResponse handleExceptionResponse(final HttpRequest httpRequest, final RuntimeException exception)
            throws IOException {
        if (exception instanceof BadRequestException) {
            return getExceptionHttpResponse(httpRequest, BAD_REQUEST);
        }
        if (exception instanceof UnAuthorisedException) {
            return getExceptionHttpResponse(httpRequest, UNAUTHORIZED);
        }
        if (exception instanceof NotFoundException) {
            return getExceptionHttpResponse(httpRequest, NOT_FOUND);
        }
        return getExceptionHttpResponse(httpRequest, INTERNAL_SERVER_ERROR);
    }

    private HttpResponse getExceptionHttpResponse(final HttpRequest httpRequest, final HttpStatusCode httpStatusCode)
            throws IOException {
        final String responseBody = loadFile("/" + httpStatusCode.getCode() + ".html");
        final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
        return new HttpResponse.Builder()
                .statusCode(httpStatusCode)
                .headers(httpHeaders)
                .body(responseBody)
                .build();
    }
}
