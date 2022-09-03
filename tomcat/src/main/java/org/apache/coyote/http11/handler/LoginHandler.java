package org.apache.coyote.http11.handler;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.support.FileReader;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.HttpResponseLine;
import org.apache.coyote.http11.model.response.HttpStatusCode;

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
        HttpResponse httpResponse = getResourcePath(httpRequest);
        return httpResponse.getResponse();
    }

    private HttpResponse getResourcePath(HttpRequest httpRequest) {
        if (httpRequest.isEmptyQueryParams()) {
            return createHttpResponse(HttpStatusCode.OK, FileReader.getFile(LOGIN_RESOURCE_PATH, getClass()));
        }
        if (checkLogin(httpRequest.getQueryParams())) {
            return createHttpResponse(HttpStatusCode.OK, FileReader.getFile(INDEX_RESOURCE_PATH, getClass()));
        }
        return createHttpResponse(HttpStatusCode.UNAUTHORIZED, FileReader.getFile(UNAUTHORIZED_RESOURCE_PATH, getClass()));
    }

    private HttpResponse createHttpResponse(HttpStatusCode httpStatusCode, String resourcePath) {
        HttpResponseLine responseLine = HttpResponseLine.of(httpStatusCode);
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
