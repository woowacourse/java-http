package org.apache.coyote.http11;

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
        if (httpRequest.isLogin()) {
            final HttpPath httpPath = httpRequest.getPath();
            final QueryString queryString = httpPath.getQueryString();

            if (httpRequest.getMethod().equals("GET") && queryString.hasNoQueryString()) {
                return new HttpResponseEntity(HttpStatus.OK, "/login.html");
            }

            final String account = queryString.get("account");
            final String password = queryString.get("password");
            System.out.println("account = " + account);
            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
            if (user.checkPassword(password)) {
                log.info("user: {}", user);
                return new HttpResponseEntity(HttpStatus.FOUND, "/index.html");
            }
            return new HttpResponseEntity(HttpStatus.UNAUTHORIZED, "/401.html");
        }

        if (httpRequest.isRegister()) {
            if (httpRequest.getMethod().equals("POST")) {

            }

            return new HttpResponseEntity(HttpStatus.OK, "/register.html");
        }

        return new HttpResponseEntity(HttpStatus.OK, httpRequest.getPath().getPath());
    }

}
