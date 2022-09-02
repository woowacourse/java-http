package org.apache.coyote.http11.uriprocessor;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.UriResponse;

public class LoginUriHandler extends DefaultUriHandler {

    @Override
    public boolean canHandle(String uri) {
        return uri.startsWith("/login");
    }

    @Override
    public UriResponse getResponse(String path, Map<String, Object> parameters) throws IOException {
        final String account = getParameter(parameters, "account");
        final String password = getParameter(parameters, "password");

        final User user = findUser(account);
        validatePassword(user, password);

        final String responseBody = getResponseBody("static/login.html");
        final String contentType = ContentType.HTML.getValue();
        System.out.println(user);

        return new UriResponse(responseBody, contentType);
    }

    private static String getParameter(Map<String, Object> parameters, String parameter) {
        String value = (String) parameters.get(parameter);
        return Objects.requireNonNull(value);
    }

    private static User findUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 account 입니다."));
    }

    private void validatePassword(User user, String password) {
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("잘못된 password 입니다.");
        }
    }
}


