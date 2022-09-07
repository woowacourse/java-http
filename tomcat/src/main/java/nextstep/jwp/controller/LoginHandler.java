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
import org.apache.coyote.utils.Util;

import java.util.Optional;

import static org.apache.coyote.utils.Util.createResponse;


public class LoginHandler extends AbstractHandler {

    private static final String LOGIN_HTML = "/login.html";
    private static final String INDEX_HTML = "/index.html";
    private static final String CLIENT_ERROR_401 = "/401.html";

    public LoginHandler(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    protected String getMethodResponse() {
        if (SessionManager.findSession(httpRequest.getCookieKey()).isPresent()) {
            return createResponse(StatusCode.OK, Util.getResponseBody(INDEX_HTML, getClass()))
                    .getResponse();
        }
        return createResponse(StatusCode.OK, Util.getResponseBody(LOGIN_HTML, getClass()))
                .getResponse();
    }

    @Override
    protected String postMethodResponse() {
        if (!checkUser(httpRequest.getRequestBody())) {
            throw new IllegalArgumentException("유저 정보가 올바르지 않습니다.");
        }
        HttpResponse httpResponse = createResponse(StatusCode.FOUND, Util.getResponseBody(INDEX_HTML, getClass()));
        if (!httpRequest.existCookie(ResponseHeader.SET_COOKIE)) {
            Cookie cookie = new Cookie();
            RequestBody requestBody = httpRequest.getRequestBody();
            SessionManager.add(cookie.toString(), new Session("user", requestBody.getByKey("account")));
            httpResponse.addCookie(cookie);
        }
        return httpResponse.getResponse();
    }

    @Override
    protected String otherMethodResponse() {
        return createResponse(StatusCode.UNAUTHORIZED, Util.getResponseBody(CLIENT_ERROR_401, getClass()))
                .getResponse();
    }

    private boolean checkUser(final RequestBody params) {
        final String account = params.getByKey("account");
        final String password = params.getByKey("password");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        return optionalUser.filter(user -> user.checkPassword(password))
                .isPresent();
    }
}
