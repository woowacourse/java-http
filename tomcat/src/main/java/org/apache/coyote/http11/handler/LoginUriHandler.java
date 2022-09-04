package org.apache.coyote.http11.handler;

import java.util.regex.Pattern;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginUriHandler implements UriHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginUriHandler.class);

    private static final Pattern LOGIN_URI_PATTERN = Pattern.compile("/login");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchUri(LOGIN_URI_PATTERN);
    }

    @Override
    public HandlerResponse getResponse(HttpRequest httpRequest) {
        final String account = (String) httpRequest.getParameter("account");
        final String password = (String) httpRequest.getParameter("password");

        final User user = findUser(account);
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return new HandlerResponse(HttpStatus.FOUND, "/index.html");
        }

        return new HandlerResponse(HttpStatus.UNAUTHORIZED, "/401.html");
    }

    private User findUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 account 입니다."));
    }
}


