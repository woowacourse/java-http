package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.request.Method;
import org.apache.coyote.model.request.RequestBody;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.ResponseHeader;
import org.apache.coyote.model.response.ResponseLine;
import org.apache.coyote.model.response.StatusCode;
import org.apache.coyote.model.session.Cookie;
import org.apache.coyote.model.session.Session;
import org.apache.coyote.model.session.SessionManager;
import org.apache.coyote.utils.RequestUtil;

import java.util.Optional;

import static org.apache.coyote.model.request.ContentType.HTML;


public class LoginHandler implements Handler {

    public static final String LOGIN_HTML = "/login.html";
    public static final String INDEX_HTML = "/index.html";
    public static final String CLIENT_ERROR_401 = "/401.html";
    private final HttpRequest httpRequest;

    public LoginHandler(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        if (httpRequest.checkMethod(Method.GET)) {
            if (SessionManager.findSession(httpRequest.getCookieKey()).isPresent()) {
                return createResponse(StatusCode.OK, RequestUtil.getResponseBody(INDEX_HTML, getClass()))
                        .getResponse();
            }
            return createResponse(StatusCode.OK, RequestUtil.getResponseBody(LOGIN_HTML, getClass()))
                    .getResponse();
        }
        if (httpRequest.checkMethod(Method.POST) && checkUser(httpRequest.getRequestBody())) {
            HttpResponse httpResponse = createResponse(StatusCode.FOUND, RequestUtil.getResponseBody(INDEX_HTML, getClass()));
            if (!httpRequest.existCookie(ResponseHeader.SET_COOKIE)) {
                Cookie cookie = new Cookie();
                RequestBody requestBody = httpRequest.getRequestBody();
                SessionManager.add(cookie.toString(), new Session("user", requestBody.getByKey("account")));
                httpResponse.addCookie(cookie);
            }
            return httpResponse.getResponse();
        }
        return createResponse(StatusCode.UNAUTHORIZED, RequestUtil.getResponseBody(CLIENT_ERROR_401, getClass()))
                .getResponse();
    }

    protected static HttpResponse createResponse(StatusCode statusCode, String responseBody) {
        ResponseLine responseLine = ResponseLine.of(statusCode);
        return HttpResponse.of(HTML.getExtension(), responseBody, responseLine);
    }

    private boolean checkUser(final RequestBody params) {
        final String account = params.getByKey("account");
        final String password = params.getByKey("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        return optionalUser.filter(user -> user.checkPassword(password))
                .isPresent();
    }
}
