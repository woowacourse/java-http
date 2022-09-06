package org.apache.coyote.http11.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.ContentType;
import org.apache.coyote.http11.http.domain.Headers;
import org.apache.coyote.http11.http.domain.MessageBody;
import org.apache.coyote.http11.util.FileReader;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        register(httpRequest);
        return HttpResponse.found(
                Headers.builder()
                        .location("/index.html"),
                new MessageBody("")
        );
    }

    private void register(final HttpRequest httpRequest) {
        Map<String, String> parameters = httpRequest.getMessageBody()
                .getParameters();
        String account = parameters.get("account");
        String password = parameters.get("password");
        String email = parameters.get("email");
        User user = new User(account, password, email);
        if (!InMemoryUserRepository.isRegistrable(account)) {
            throw new IllegalArgumentException("Account already exists");
        }
        InMemoryUserRepository.save(user);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        String uri = httpRequest.getRequestLine().getRequestTarget().getUri();
        String responseBody = FileReader.read(uri + ".html");
        return HttpResponse.ok(ContentType.from(uri), new MessageBody(responseBody));
    }
}
