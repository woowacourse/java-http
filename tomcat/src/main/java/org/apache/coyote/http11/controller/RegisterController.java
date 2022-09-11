package org.apache.coyote.http11.controller;

import com.sun.jdi.InternalException;
import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.model.User;
import nextstep.jwp.model.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController implements Handler {

    private static final String SUCCEED_REDIRECT_URL = "/index.html";
    private static final String REGISTER_PAGE_URL = "/register.html";

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            if (httpRequest.requestPOST() && !httpRequest.getRequestBody().isEmpty()) {
                final User user = UserService.getInstance().register(httpRequest.getRequestBody());
                httpResponse.setSessionAndCookieWithOkResponse(user, SUCCEED_REDIRECT_URL);
                return;
            }
            httpResponse.setOkResponse(REGISTER_PAGE_URL);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new InternalException("서버 에러가 발생했습니다.");
        }
    }
}
