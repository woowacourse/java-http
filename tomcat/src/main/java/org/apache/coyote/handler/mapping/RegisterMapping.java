package org.apache.coyote.handler.mapping;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.HttpMethod;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.coyote.http.HttpMethod.POST;

public class RegisterMapping implements HandlerMapping {

    @Override
    public boolean supports(final HttpMethod httpMethod, final String requestUri) {
        return POST == httpMethod &&
                requestUri.contains("register");
    }

    @Override
    public String handle(final String requestUri, final Map<String, String> headers, final String requestBody) {
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
