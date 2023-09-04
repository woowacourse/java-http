package org.apache.coyote.handler.mapping;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RegisterMapping implements HandlerMapping {

    @Override
    public boolean supports(final String httpMethod, final String requestUri) {
        return "POST".equals(httpMethod) &&
                requestUri.contains("register");
    }

    @Override
    public String handle(final String requestUri, final Map<String, String> headers, final String requestBody) throws IOException {
        final Map<String, String> bodyParams = Arrays.stream(requestBody.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));

        final String account = bodyParams.get("account");
        final String password = bodyParams.get("password");
        final String email = bodyParams.get("email");

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ");
    }
}
