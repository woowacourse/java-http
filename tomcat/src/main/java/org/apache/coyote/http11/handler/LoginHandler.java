package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.Handler;
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
        logAccount(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType("text/html;charset=utf-8");
        HttpResponse response = new HttpResponse(HttpProtocol.HTTP11, HttpStatus.OK, headers);
        String contentBody = ResourceResolver.resolve("/login.html");
        response.setContentBody(contentBody);
        return response;
    }

    private void logAccount(HttpRequest request) {
        String account = getQueryString(request, "account");
        String password = getQueryString(request, "password");
        InMemoryUserRepository.findByAccount(account)
            .ifPresent(user -> {
                if (user.checkPassword(password)) {
                    log.info("{}", user);
                }
            });
    }

    private String getQueryString(HttpRequest request, String key) {
        List<String> queryString = request.getQueryString(key);
        if (queryString.isEmpty()) {
            return "";
        }
        return queryString.get(0);
    }
}
