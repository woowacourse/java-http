package org.apache.coyote.http11.controller;

import com.sun.jdi.InternalException;
import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.model.User;
import nextstep.jwp.model.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController implements Handler {

    private static final String SUCCEED_REDIRECT_URL = "/index.html";
    private static final String FAILED_REDIRECT_URL = "/401.html";
    private static final String LOGIN_HTML_URL = "/login.html";

    @Override
    public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            if (isRequestLoginPage(httpRequest)) {
                httpResponse.setOkResponse(LOGIN_HTML_URL);
                return;
            }
            final User succeedLoginUser = UserService.getInstance().login(httpRequest.getParams());

            if (isSucceedLogin(succeedLoginUser)) {
                httpResponse.setSessionAndCookieWithOkResponse(succeedLoginUser, SUCCEED_REDIRECT_URL);
                return;
            }
            httpResponse.setFoundResponse(FAILED_REDIRECT_URL);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new InternalException("서버 에러가 발생했습니다.");
        }
    }

    private boolean isSucceedLogin(final User succeedLoginUser) {
        return succeedLoginUser != null;
    }

    private boolean isRequestLoginPage(HttpRequest httpRequest) {
        return httpRequest.getParams().isEmpty() && !httpRequest.hasCookie();
    }
}
