package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Handler;
import org.apache.coyote.common.HttpContentType;
import org.apache.coyote.common.HttpHeaders;
import org.apache.coyote.common.HttpProtocol;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.util.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        String account = getQueryString(request, "account");
        String password = getQueryString(request, "password");
        if (account.isBlank() || password.isBlank()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(HttpContentType.TEXT_HTML);
            HttpResponse response = new HttpResponse(HttpProtocol.HTTP11, HttpStatus.OK, headers);
            response.setContentBody(ResourceResolver.resolve("/login.html"));
            return response;
        }
        return InMemoryUserRepository.findByAccount(account)
            .filter(user -> user.checkPassword(password))
            .map(this::loginSuccess)
            .orElseGet(this::loginFail);
    }

    private HttpResponse loginSuccess(User user) {
        log.info("{}", user);
        HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Location", "/index.html");
        return new HttpResponse(HttpProtocol.HTTP11, HttpStatus.FOUND, headers);
    }

    private HttpResponse loginFail() {
        HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Location", "/401.html");
        return new HttpResponse(HttpProtocol.HTTP11, HttpStatus.FOUND, headers);
    }

    private String getQueryString(HttpRequest request, String key) {
        List<String> queryString = request.getQueryString(key);
        if (queryString.isEmpty()) {
            return "";
        }
        return queryString.get(0);
    }
}
