package org.apache.coyote.http11.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;

public class LoginHandler extends Handler {

    static LoginHandler LOGIN_HANDLER = new LoginHandler();

    private static String JSESSIONID = "JSESSIONID";
    private static String SUCCESS_REDIRECT_URI = "/index.html";
    private static String FAIL_REDIRECT_URI = "/401.html";

    private LoginHandler() {
    }

    @Override
    public HandlerResult handle(final HttpRequest request) {
        try {
            if (request.getCookieValue(JSESSIONID) != null) {
                throw new IllegalArgumentException("이미 로그인이 되어있습니다.");
            }

            final User loginUser = InMemoryUserRepository.findByAccount(request.getBodyValue("account"))
                    .orElseThrow(() -> new IllegalArgumentException("없는 회원입니다."));

            if (!loginUser.checkPassword(request.getBodyValue("password"))) {
                throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
            }

            log.info("user : " + loginUser);
            return createHandlerResult(HttpStatusCode.FOUND, SUCCESS_REDIRECT_URI, String.valueOf(UUID.randomUUID()));
        } catch (IllegalArgumentException e) {
            return createHandlerResult(HttpStatusCode.FOUND, FAIL_REDIRECT_URI);
        }
    }

    private HandlerResult createHandlerResult(final HttpStatusCode statusCode, final String redirectUri,
                                              final String jSessionId) {
        final Map<String, String> responseHeader = new LinkedHashMap<>();
        responseHeader.put("Location", redirectUri);
        responseHeader.put("Set-Cookie", JSESSIONID + "=" + jSessionId);
        return new HandlerResult(statusCode, responseHeader, "");
    }

    private HandlerResult createHandlerResult(final HttpStatusCode statusCode, final String redirectUri) {
        final Map<String, String> responseHeader = new LinkedHashMap<>();
        responseHeader.put("Location", redirectUri);
        return new HandlerResult(statusCode, responseHeader, "");
    }
}
