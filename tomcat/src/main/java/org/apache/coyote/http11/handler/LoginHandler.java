package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.request.Param;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.HttpStatusCode;
import org.apache.coyote.model.response.StatusCode;
import org.apache.coyote.utils.RequestUtil;

import java.util.Optional;

import static org.apache.coyote.model.request.ContentType.HTML;


public class LoginHandler implements Handler {

    public static final String LOGIN_HTML = "/login.html";
    private final HttpRequest httpRequest;

    public LoginHandler(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        if (httpRequest.checkEmptyParams()) {
            return createResponse(StatusCode.OK, RequestUtil.getResponseBody("/login.html", getClass()));
        }
        if (checkUser(httpRequest.getParams())) {
            return createResponse(StatusCode.FOUND, RequestUtil.getResponseBody("/index.html", getClass()));
        }
        return createResponse(StatusCode.UNAUTHORIZED, RequestUtil.getResponseBody("/401.html", getClass()));
    }

    private String createResponse(StatusCode statusCode, String responseBody) {
        HttpStatusCode httpStatusCode = HttpStatusCode.of(statusCode);
        return HttpResponse.of(HTML.getExtension(), responseBody, httpStatusCode)
                .getResponse();
    }

    private boolean checkUser(final Param params) {
        final String account = params.getByKey("account");
        final String password = params.getByKey("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        return optionalUser.filter(user -> user.checkPassword(password))
                .isPresent();
    }
}
