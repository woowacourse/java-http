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
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.badrequest.AlreadyRegisterAccountException;
import org.apache.coyote.http11.exception.badrequest.BadRequestException;
import org.apache.coyote.http11.exception.notfound.NotFoundException;
import org.apache.coyote.http11.exception.notfound.NotFoundUrlException;
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
            return getExceptionHttpResponse(httpRequest, exception);
        }
    }

    private HttpResponse getHttpResponse(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isFileRequest()) {
            final String responseBody = loadFile(httpRequest.getUrl());
            final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
            return new HttpResponse.Builder()
                    .statusCode(OK)
                    .headers(httpHeaders)
                    .body(responseBody)
                    .build();
        }
        if (httpRequest.getUrl().equals("/")) {
            final String responseBody = "Hello world!";
            final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
            return new HttpResponse.Builder()
                    .statusCode(OK)
                    .headers(httpHeaders)
                    .body(responseBody)
                    .build();
        }
        if (httpRequest.getUrl().equals("/login")) {
            if (!httpRequest.getQueryString("account").isBlank() && !httpRequest.getQueryString("password").isBlank()) {
                validateLogin(httpRequest);
                final HttpHeaders httpHeaders = new HttpHeaders()
                        .addHeader(LOCATION, "/index.html");
                return new HttpResponse.Builder()
                        .statusCode(FOUND)
                        .headers(httpHeaders)
                        .build();
            }
            final String responseBody = loadFile("/login.html");
            final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
            return new HttpResponse.Builder()
                    .statusCode(OK)
                    .headers(httpHeaders)
                    .body(responseBody)
                    .build();
        }
        if (httpRequest.getUrl().equals("/register")) {
            if (httpRequest.getHttpMethod() == GET) {
                final String responseBody = loadFile("/register.html");
                final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
                return new HttpResponse.Builder()
                        .statusCode(OK)
                        .headers(httpHeaders)
                        .body(responseBody)
                        .build();
            }
            if (httpRequest.getHttpMethod() == POST) {
                final HttpRequestBody httpRequestBody = httpRequest.getHttpRequestBody();
                validateRegistrable(httpRequestBody.getBodyValue("account"));
                registerNewUser(httpRequestBody);
                final HttpHeaders httpHeaders = new HttpHeaders()
                        .addHeader(LOCATION, "/index.html");
                return new HttpResponse.Builder()
                        .statusCode(FOUND)
                        .headers(httpHeaders)
                        .build();
            }
        }
        throw new NotFoundUrlException();
    }

    private void validateLogin(final HttpRequest httpRequest) {
        final String account = httpRequest.getQueryString("account");
        final String password = httpRequest.getQueryString("password");
        final User user = findRegisteredUser(account);
        if (!user.checkPassword(password)) {
            throw new LoginFailException();
        }
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

    private HttpHeaders getHttpHeaders(final HttpRequest httpRequest, final String responseBody) {
        return new HttpHeaders()
                .addHeader(CONTENT_TYPE, getContentType(httpRequest))
                .addHeader(CONTENT_LENGTH, responseBody.getBytes().length);
    }

    private String getContentType(final HttpRequest httpRequest) {
        return "text/" + httpRequest.getFileExtension() + ";charset=utf-8";
    }

    private HttpResponse getExceptionHttpResponse(final HttpRequest httpRequest, final RuntimeException exception)
            throws IOException {
        if (exception instanceof BadRequestException) {
            final String responseBody = loadFile("/400.html");
            final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
            return new HttpResponse.Builder()
                    .statusCode(BAD_REQUEST)
                    .headers(httpHeaders)
                    .body(responseBody)
                    .build();
        }
        if (exception instanceof UnAuthorisedException) {
            final String responseBody = loadFile("/401.html");
            final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
            return new HttpResponse.Builder()
                    .statusCode(UNAUTHORIZED)
                    .headers(httpHeaders)
                    .body(responseBody)
                    .build();
        }
        if (exception instanceof NotFoundException) {
            final String responseBody = loadFile("/404.html");
            final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
            return new HttpResponse.Builder()
                    .statusCode(NOT_FOUND)
                    .headers(httpHeaders)
                    .body(responseBody)
                    .build();
        }
        final String responseBody = loadFile("/500.html");
        final HttpHeaders httpHeaders = getHttpHeaders(httpRequest, responseBody);
        return new HttpResponse.Builder()
                .statusCode(INTERNAL_SERVER_ERROR)
                .headers(httpHeaders)
                .body(responseBody)
                .build();
    }
}
