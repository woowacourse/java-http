package org.apache.coyote.http11;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final HttpRequest httpRequest;

    public HttpRequestHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public HttpResponseEntity handle() {
        final HttpRequestBody body = httpRequest.getBody();

        if (httpRequest.isLogin()) {
            if (httpRequest.getMethod().equals("GET")) {
                return new HttpResponseEntity(HttpStatus.OK, "/login.html");
            }

            final Map<String, String> userInfos = body.parseUserInfos();
            final String account = userInfos.get("account");
            final String password = userInfos.get("password");
            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
            if (user.checkPassword(password)) {
                log.info("로그인 성공! 아이디: {}", account);
                return new HttpResponseEntity(HttpStatus.FOUND, "/index.html");
            }
            return new HttpResponseEntity(HttpStatus.UNAUTHORIZED, "/401.html");
        }

        if (httpRequest.isRegister()) {
            if (httpRequest.getMethod().equals("POST")) {
                final Map<String, String> userInfos = body.parseUserInfos();
                final String account = userInfos.get("account");
                final String password = userInfos.get("password");
                final String email = userInfos.get("email");

                if (InMemoryUserRepository.findByAccount(account).isEmpty()) {
                    final User user = new User(account, password, email);
                    InMemoryUserRepository.save(user);
                    return new HttpResponseEntity(HttpStatus.OK, "/index.html");
                }
            }

            return new HttpResponseEntity(HttpStatus.OK, "/register.html");
        }

        return new HttpResponseEntity(HttpStatus.OK, httpRequest.getPath().getPath());
    }

}
