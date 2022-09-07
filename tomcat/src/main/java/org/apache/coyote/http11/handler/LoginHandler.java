package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;

public class LoginHandler extends Handler {

    static LoginHandler LOGIN_HANDLER = new LoginHandler();

    private static String SUCCESS_REDIRECT_URI = "/index.html";
    private static String FAIL_REDIRECT_URI = "/401.html";

    private LoginHandler() {
    }

    @Override
    public HandlerResult handle(final HttpRequest request) {
        try {
            final HttpRequestBody requestBody = request.getBody();
            final User loginUser = InMemoryUserRepository.findByAccount(requestBody.getInfo("account"))
                    .orElseThrow(() -> new IllegalArgumentException("없는 회원입니다."));

            if (!loginUser.checkPassword(requestBody.getInfo("password"))) {
                throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
            }

            log.info("user : " + loginUser);
            return createHandlerResult(HttpStatusCode.FOUND, SUCCESS_REDIRECT_URI);
        } catch (IllegalArgumentException e) {
            return createHandlerResult(HttpStatusCode.FOUND, FAIL_REDIRECT_URI);
        }
    }

    private HandlerResult createHandlerResult(final HttpStatusCode statusCode, final String redirectUri) {
        final Map<String, String> responseHeader = new HashMap<>();
        responseHeader.put("Location", redirectUri);
        return new HandlerResult(statusCode, responseHeader, "");
    }
}
