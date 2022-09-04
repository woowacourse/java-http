package org.apache.coyote.http11.handler;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.handler.support.FileReader;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.Method;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseLine;
import org.apache.coyote.http11.model.response.ResponseStatusCode;

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
        HttpResponse httpResponse = getHttpResponse(httpRequest);
        return httpResponse.getResponse();
    }

    private HttpResponse getHttpResponse(HttpRequest httpRequest) {
        if (httpRequest.matchRequestMethod(Method.GET)) {
            return doGetMethod(httpRequest);
        }
        return doPostMethod(httpRequest);
    }

    private HttpResponse doPostMethod(final HttpRequest httpRequest) {
        Map<String, String> body = httpRequest.getBody();
        Optional<User> maybeUser = InMemoryUserRepository.findByAccount(body.get("account"));
        if (httpRequest.matchRequestMethod(Method.POST) && maybeUser.isPresent() && checkUserPassword(body, maybeUser.get())) {
            HttpResponse httpResponse = createHttpResponse(ResponseStatusCode.OK,
                    FileReader.getFile(INDEX_RESOURCE_PATH, getClass()));
            if (!httpRequest.hasCookie()) {
                Cookie cookie = new Cookie();
                httpResponse.addCookie(cookie);
                SessionManager.add(cookie.toString(), new Session("user", maybeUser.get()));
            }
            return httpResponse;
        }
        return createHttpResponse(ResponseStatusCode.UNAUTHORIZED,
                FileReader.getFile(UNAUTHORIZED_RESOURCE_PATH, getClass()));
    }

    private HttpResponse doGetMethod(final HttpRequest httpRequest) {
        if (SessionManager.findSession(httpRequest.getCookieKey())
                .isPresent()) {
            return createHttpResponse(ResponseStatusCode.OK,
                    FileReader.getFile(INDEX_RESOURCE_PATH, getClass()));
        }
        return createHttpResponse(ResponseStatusCode.OK, FileReader.getFile(LOGIN_RESOURCE_PATH, getClass()));
    }

    private HttpResponse createHttpResponse(ResponseStatusCode responseStatusCode, String resourcePath) {
        ResponseLine responseLine = ResponseLine.of(responseStatusCode);
        return HttpResponse.of(responseLine, ContentType.HTML, resourcePath);
    }

    private boolean checkLogin(final Map<String, String> queryParams) {
        String account = queryParams.get("account");
        Optional<User> maybeUser = InMemoryUserRepository.findByAccount(account);
        if (maybeUser.isEmpty()) {
            return false;
        }
        return checkUserPassword(queryParams, maybeUser.get());
    }

    private boolean checkUserPassword(final Map<String, String> queryParams, final User user) {
        String password = queryParams.get("password");
        return user.checkPassword(password);
    }
}
