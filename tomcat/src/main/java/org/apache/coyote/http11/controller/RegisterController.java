package org.apache.coyote.http11.controller;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Request.HttpRequest;
import org.apache.coyote.http11.Response.HttpResponse;
import org.apache.coyote.http11.model.Parameters;
import org.apache.coyote.http11.model.Path;
import org.apache.coyote.http11.utils.Files;

public final class RegisterController extends AbstractController {
    @Override
    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        final Parameters loginParameters = Parameters.parseParameters(request.getRequestBody(), "&");
        final String account = loginParameters.get("account");
        final String password = loginParameters.get("password");
        final String email = loginParameters.get("email");

        if (InMemoryUserRepository.existsByAccount(account)) {
            throw new IllegalArgumentException("이미 등록된 Account입니다. [account : " + account + "]");
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return HttpResponse.found("/index.html");
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        final String path = Path.from(request.getPath());
        final String body = Files.readFile(path);
        return HttpResponse.ok(body);
    }
}
