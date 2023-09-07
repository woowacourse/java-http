package org.apache.coyote.handler.mapping.login;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.mapping.HandlerMapping;
import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.common.HttpHeader;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.apache.coyote.handler.mapping.Path.MAIN;
import static org.apache.coyote.handler.mapping.Path.UNAUTHORIZED;

public class LoginMapping extends LoginFilter implements HandlerMapping {

    private static final String TARGET_URI = "login";
    private static final Logger log = LoggerFactory.getLogger(LoginMapping.class);

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isPostRequest() && httpRequest.containsRequestUri(TARGET_URI);
    }

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
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
            return HttpResponse.redirect(UNAUTHORIZED.getPath());
        }

        final UUID uuid = UUID.randomUUID();
        setSession(uuid.toString(), Map.of("account", user.getAccount()));

        return HttpResponse.builder()
                .statusLine(StatusLine.from(StatusCode.FOUND))
                .httpHeaders(HttpHeader.LOCATION, MAIN.getPath())
                .httpHeaders(HttpHeader.SET_COOKIE, "JSESSIONID=" + uuid)
                .body(HttpBody.empty())
                .build();
    }
}
