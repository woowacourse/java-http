package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemorySessionRepository;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.Session;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.exception.ResourceLoadingException;
import org.apache.coyote.http11.header.HeaderName;
import org.apache.coyote.http11.parser.CookieParser;
import org.apache.coyote.http11.parser.FormDataParser;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String SESSION_COOKIE_KEY = "JSESSIONID";
    private static final String INDEX_PAGE_RESOURCE = "/index.html";
    private static final String LOGIN_PAGE_RESOURCE = "/login.html";
    private static final String UNAUTHORIZED_PAGE_RESOURCE = "/401.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String resourcepath = LOGIN_PAGE_RESOURCE;
        try {
            if (isSessionValid(request)) {
                resourcepath = INDEX_PAGE_RESOURCE;
                final HttpResponse newResponse =
                    StaticResourceResponseSupplier.getWithExtensionContentType(resourcepath);
                response.update(newResponse);
                return;
            }
            final HttpResponse newResponse =
                StaticResourceResponseSupplier.getWithExtensionContentType(resourcepath);

            response.update(newResponse);
        } catch (IOException e) {
            throw new ResourceLoadingException(resourcepath);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        final Map<String, String> formData = FormDataParser.parse(request.getBody());
        final String account = formData.get("account");
        final String password = formData.get("password");

        final Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
        if (findUser.isPresent() && findUser.get().checkPassword(password)) {
            loginSuccess(response, findUser.get());
            return;
        }
        loginFail(request, response);
    }

    private boolean isSessionValid(final HttpRequest httpRequest) {
        if (httpRequest.containsHeader(HeaderName.COOKIE.getValue())) {
            final String cookieValue = httpRequest.getHeader(HeaderName.COOKIE.getValue());
            final Map<String, String> cookieValues = CookieParser.parse(cookieValue);

            if (cookieValues.containsKey(SESSION_COOKIE_KEY)) {
                final String sessionId = cookieValues.get(SESSION_COOKIE_KEY);

                return InMemorySessionRepository.isExist(new Session(sessionId.strip()));
            }
        }
        return false;
    }

    private void loginSuccess(final HttpResponse response, final User user) {
        log.info("login user = {}", user);
        final Session session = InMemorySessionRepository.findByUser(user)
            .orElseGet(() -> createNewSession(user));

        final HttpResponse newResponse = HttpResponse.redirect(INDEX_PAGE_RESOURCE);
        newResponse.addCookie(SESSION_COOKIE_KEY, session.getUuid());

        response.update(newResponse);
    }

    private Session createNewSession(final User user) {
        return InMemorySessionRepository.save(user, new Session(UUID.randomUUID().toString()));
    }

    private void loginFail(final HttpRequest request, final HttpResponse response) {
        try {
            final HttpResponse newResponse =
                StaticResourceResponseSupplier.getWithExtensionContentType(
                    UNAUTHORIZED_PAGE_RESOURCE
                );
            response.update(newResponse);
        } catch (IOException e) {
            throw new ResourceLoadingException(request.getPath());
        }
    }
}
