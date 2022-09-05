package org.apache.coyote.http11.handler;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.support.FileReader;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.Method;
import org.apache.coyote.http11.model.response.ContentType;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseStatusCode;
import org.apache.coyote.http11.model.session.Cookie;
import org.apache.coyote.http11.model.session.Session;
import org.apache.coyote.http11.model.session.SessionManager;

public class LoginHandler implements Handler {

    private static final String LOGIN_RESOURCE_PATH = "/login.html";
    private static final String INDEX_RESOURCE_PATH = "/index.html";
    private static final String UNAUTHORIZED_RESOURCE_PATH = "/401.html";

    private final HttpRequest httpRequest;

    public LoginHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        if (httpRequest.matchRequestMethod(Method.GET)) {
            return doGet(httpRequest).getResponse();
        }
        if (httpRequest.matchRequestMethod(Method.POST)) {
            return doPost(httpRequest).getResponse();
        }
        throw new IllegalArgumentException("올바르지 않은 Method 요청입니다.");
    }

    private HttpResponse doGet(final HttpRequest httpRequest) {
        if (SessionManager.findSession(httpRequest.getCookieKey())) {
            HttpResponse response = HttpResponse.of(ResponseStatusCode.FOUND, ContentType.HTML);
            response.addLocationHeader(INDEX_RESOURCE_PATH);
            return response;

        }
        return HttpResponse.of(ResponseStatusCode.OK, ContentType.HTML,
                FileReader.getFile(LOGIN_RESOURCE_PATH, getClass()));
    }

    private HttpResponse doPost(final HttpRequest httpRequest) {
        Map<String, String> body = httpRequest.getBody();
        Optional<User> maybeUser = InMemoryUserRepository.findByAccount(body.get("account"));

        if (successLogin(body, maybeUser)) {
            return successResponse(maybeUser.get());
        }

        HttpResponse httpResponse = HttpResponse.of(ResponseStatusCode.UNAUTHORIZED, ContentType.HTML);
        httpResponse.addLocationHeader(UNAUTHORIZED_RESOURCE_PATH);
        return httpResponse;
    }

    private boolean successLogin(final Map<String, String> body, final Optional<User> maybeUser) {
        return maybeUser.isPresent() && checkUserPassword(body, maybeUser.get());
    }

    private HttpResponse successResponse(final User user) {
        HttpResponse httpResponse = HttpResponse.of(ResponseStatusCode.FOUND, ContentType.HTML);
        Cookie cookie = new Cookie();
        SessionManager.add(cookie.getCookieToString(), new Session("user", user));
        httpResponse.addCookie(cookie);
        httpResponse.addLocationHeader(INDEX_RESOURCE_PATH);
        return httpResponse;
    }

    private boolean checkUserPassword(final Map<String, String> queryParams, final User user) {
        String password = queryParams.get("password");
        return user.checkPassword(password);
    }
}
