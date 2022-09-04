package org.apache.coyote.http11.handler;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Cookie;
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
            return createHttpResponse(ResponseStatusCode.OK, FileReader.getFile(LOGIN_RESOURCE_PATH, getClass()));
        }
        if (httpRequest.matchRequestMethod(Method.POST) && checkLogin(httpRequest.getBody())) {
            HttpResponse httpResponse = createHttpResponse(ResponseStatusCode.OK,
                    FileReader.getFile(INDEX_RESOURCE_PATH, getClass()));
            if (!httpRequest.hasCookie()) {
                httpResponse.addCookie(new Cookie());
            }
            return httpResponse;
        }
        return createHttpResponse(ResponseStatusCode.UNAUTHORIZED,
                FileReader.getFile(UNAUTHORIZED_RESOURCE_PATH, getClass()));
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
