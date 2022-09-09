package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.model.request.HttpRequest;
import org.apache.coyote.model.request.RequestBody;
import org.apache.coyote.model.response.HttpResponse;
import org.apache.coyote.model.response.ResponseHeader;
import org.apache.coyote.model.response.StatusCode;
import org.apache.coyote.model.session.Cookie;
import org.apache.coyote.model.session.Session;
import org.apache.coyote.model.session.SessionManager;

import java.util.Optional;

import static org.apache.coyote.model.response.HttpResponse.createResponse;
import static org.apache.coyote.model.response.HttpResponse.getResponseBody;


public class LoginHandler extends AbstractHandler {

    private static final LoginHandler INSTANCE = new LoginHandler();
    private static final String LOGIN_HTML = "/login.html";
    private static final String INDEX_HTML = "/index.html";
    private static final String CLIENT_ERROR_401 = "/401.html";

    public static LoginHandler getINSTANCE() {
        return INSTANCE;
    }

    @Override
    protected String getMethodResponse(final HttpRequest httpRequest) {
        if (hasCookie(httpRequest) && checkCookie(httpRequest)) {
            HttpResponse httpResponse = createResponse(StatusCode.OK, getResponseBody(INDEX_HTML, getClass()));
            httpResponse.setHeader(ResponseHeader.LOCATION, INDEX_HTML);
            return httpResponse.getResponse();
        }
        return createResponse(StatusCode.OK, getResponseBody(LOGIN_HTML, getClass()))
                .getResponse();
    }

    private boolean hasCookie(HttpRequest httpRequest) {
        return httpRequest.getCookieKey() != null;
    }

    private boolean checkCookie(HttpRequest httpRequest) {
        return SessionManager.findSession(httpRequest.getCookieKey()).isPresent();
    }

    @Override
    protected String postMethodResponse(final HttpRequest httpRequest) {
        if (!checkUser(httpRequest.getRequestBody())) {
            return createResponse(StatusCode.UNAUTHORIZED, getResponseBody(CLIENT_ERROR_401, getClass()))
                    .getResponse();
        }

        HttpResponse httpResponse = createResponse(StatusCode.FOUND, getResponseBody(INDEX_HTML, getClass()));
        httpResponse.setHeader(ResponseHeader.LOCATION, INDEX_HTML);
        if (!httpRequest.existCookie(ResponseHeader.SET_COOKIE)) {
            Cookie cookie = new Cookie();
            RequestBody requestBody = httpRequest.getRequestBody();
            SessionManager.add(cookie.toString(), new Session("user", requestBody.getByKey("account")));
            httpResponse.addCookie(cookie);
        }
        return httpResponse.getResponse();
    }

    private boolean checkUser(final RequestBody params) {
        final String account = params.getByKey("account");
        final String password = params.getByKey("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        return optionalUser.filter(user -> user.checkPassword(password))
                .isPresent();
    }
}
