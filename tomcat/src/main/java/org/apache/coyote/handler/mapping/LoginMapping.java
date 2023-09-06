package org.apache.coyote.handler.mapping;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class LoginMapping extends LoginFilter implements HandlerMapping {

    public static final String TARGET_URI = "login";
    private static final Logger log = LoggerFactory.getLogger(LoginMapping.class);

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isPostRequest() && httpRequest.containsRequestUri(TARGET_URI);
    }

    @Override
    public String handle(final HttpRequest httpRequest) {
        final Map<String, String> bodyParams = httpRequest.getParsedBody();
        final String account = bodyParams.get("account");
        final String password = bodyParams.get("password");

        User user = null;
        try {
            user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 계정입니다. 다시 입력해주세요."));

            if (!user.checkPassword(password)) {
                throw new IllegalArgumentException("잘못된 비밀번호입니다. 다시 입력해주세요.");
            }
            log.info("로그인 성공! user = {}", user);
        } catch (final IllegalArgumentException e) {
            log.warn("login error = {}", e);
            return String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: /401.html ");
        }

        final UUID uuid = UUID.randomUUID();
        setSession(uuid.toString(), Map.of("account", user.getAccount()));

        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Set-Cookie: JSESSIONID=" + uuid + " ");
    }
}
