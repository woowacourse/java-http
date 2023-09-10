package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.Method;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.response.StatusCode;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;

public class ResponseGenerator {

    private static final Logger log = LoggerFactory.getLogger(ResponseGenerator.class);
    public static final String ROOT_PATH = "/";
    private static final String STATIC_PATH = "static";
    private static final String LOGIN_URI = "/login";
    private static final String REGISTER_URI = "/register";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";
    private static final String COOKIE = "Cookie";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String SESSION_USER_KEY = "user";

    private ResponseGenerator() {
    }

    public static String generate(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isSameUri(ROOT_PATH)) {
            final HttpResponse httpResponse = getDefaultResponse();
            return httpResponse.toMessage();
        }
        if (httpRequest.isSameUri(LOGIN_URI)) {
            final HttpResponse httpResponse = getLoginResponse(httpRequest);
            return httpResponse.toMessage();
        }
        if (httpRequest.isSameUri(REGISTER_URI)) {
            final HttpResponse httpResponse = getRegisterResponse(httpRequest);
            return httpResponse.toMessage();
        }

        final HttpResponse httpResponse = getFileResponse(httpRequest);
        return httpResponse.toMessage();
    }

    private static HttpResponse getDefaultResponse() {
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.HTML;
        final String responseBody = "Hello world!";

        return HttpResponse.of(statusLine, contentType, responseBody);
    }

    private static HttpResponse getLoginResponse(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isSameMethod(Method.GET)) {
            return getLoginResponseGetMethod(httpRequest);
        }

        return getLoginResponsePostMethod(httpRequest);
    }

    private static HttpResponse getLoginResponseGetMethod(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.hasHeaderBy(COOKIE) && httpRequest.hasCookieKey(JSESSIONID)) {
            final String jsessionid = httpRequest.getCookieValue(JSESSIONID);
            final Session session = SessionManager.findSession(jsessionid);
            final User user = (User) session.getAttribute(SESSION_USER_KEY);

            return getLoginResponse(user.getAccount(), user.getPassword());
        }

        if (httpRequest.hasQueryString()) {
            return getLoginResponse(httpRequest.getQueryValue(ACCOUNT_KEY), httpRequest.getQueryValue(PASSWORD_KEY));
        }

        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.HTML;
        final String responseBody = getFileToResponseBody("/login.html");

        return HttpResponse.of(statusLine, contentType, responseBody);
    }

    private static HttpResponse getLoginResponse(final String account, final String password) {
        return InMemoryUserRepository.findByAccount(account)
                                     .filter(user -> user.checkPassword(password))
                                     .map(ResponseGenerator::loginSuccess)
                                     .orElseGet(() -> getRedirectResponse("/401.html"));
    }

    private static HttpResponse loginSuccess(final User user) {
        log.info(user.toString());

        final HttpResponse httpResponse = getRedirectResponse("/index.html");

        final Session session = generateJsession(user);
        httpResponse.setCookie(session.getId());

        return httpResponse;
    }

    private static Session generateJsession(final User user) {
        final var uuid = UUID.randomUUID().toString();
        final var session = new Session(uuid);
        session.addAttribute(SESSION_USER_KEY, user);
        SessionManager.add(session);
        return session;
    }

    private static HttpResponse getRedirectResponse(final String location) {
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, StatusCode.FOUND);

        return HttpResponse.ofRedirect(statusLine, location);
    }

    private static HttpResponse getLoginResponsePostMethod(final HttpRequest httpRequest) {
        return getLoginResponse(httpRequest.getBodyValue(ACCOUNT_KEY), httpRequest.getBodyValue(PASSWORD_KEY));
    }

    private static HttpResponse getRegisterResponse(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isSameMethod(Method.GET)) {
            return getRegisterResponseGetMethod();
        }

        return getRegisterResponsePostMethod(httpRequest);
    }

    private static HttpResponse getRegisterResponseGetMethod() throws IOException {
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.HTML;
        final String responseBody = getFileToResponseBody("/register.html");

        return HttpResponse.of(statusLine, contentType, responseBody);
    }

    private static HttpResponse getRegisterResponsePostMethod(final HttpRequest httpRequest) {
        final String account = httpRequest.getBodyValue(ACCOUNT_KEY);
        final String password = httpRequest.getBodyValue(PASSWORD_KEY);
        final String email = httpRequest.getBodyValue(EMAIL_KEY);
        InMemoryUserRepository.save(new User(account, password, email));

        return getRedirectResponse("/index.html");
    }

    private static HttpResponse getFileResponse(final HttpRequest httpRequest) throws IOException {
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.findBy(httpRequest.getPath());
        final String responseBody = getFileToResponseBody(httpRequest.getPath());

        return HttpResponse.of(statusLine, contentType, responseBody);
    }

    private static String getFileToResponseBody(final String fileName) throws IOException {
        final String path = STATIC_PATH + fileName;
        final URL resource = ClassLoader.getSystemClassLoader().getResource(path);
        final String filePath = Objects.requireNonNull(resource).getPath();
        final File file = new File(URLDecoder.decode(filePath, StandardCharsets.UTF_8));

        return new String(Files.readAllBytes(file.toPath()));
    }
}
