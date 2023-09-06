package org.apache.coyote.handler.mapping;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

import java.io.IOException;
import java.util.Map;

public class RegisterMapping implements HandlerMapping {

    public static final String TARGET_URI = "register";

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isPostRequest() && httpRequest.containsRequestUri(TARGET_URI);
    }

    @Override
    public String handle(final HttpRequest httpRequest) throws IOException {
        final Map<String, String> bodyParams = httpRequest.getParsedBody();

        final String account = bodyParams.get("account");
        final String password = bodyParams.get("password");
        final String email = bodyParams.get("email");

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ");
    }

    @Override
    public HttpResponse handle2(final HttpRequest httpRequest) throws IOException {
        final Map<String, String> bodyParams = httpRequest.getParsedBody();

        final String account = bodyParams.get("account");
        final String password = bodyParams.get("password");
        final String email = bodyParams.get("email");

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return HttpResponse.redirect("/index.html");
    }
}
