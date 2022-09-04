package org.apache.coyote.http11.urihandler;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.UriResponse;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginUriHandler extends DefaultUriHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginUriHandler.class);

    private static final Pattern LOGIN_URI_PATTERN = Pattern.compile("/login");

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return httpRequest.matchUri(LOGIN_URI_PATTERN);
    }

    @Override
    public UriResponse getResponse(HttpRequest httpRequest) throws IOException {
        final String account = (String) httpRequest.getParameter("account");
        final String password = (String) httpRequest.getParameter("password");

        final User user = findUser(account);
        validatePassword(user, password);

        final String responseBody = getResponseBody("static/login.html");
        final String contentType = ContentType.HTML.getValue();

        log.info(user.toString());

        return new UriResponse(responseBody, contentType);
    }

    private User findUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 account 입니다."));
    }

    private void validatePassword(User user, String password) {
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("잘못된 password 입니다.");
        }
    }
}


