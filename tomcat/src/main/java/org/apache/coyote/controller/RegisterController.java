package org.apache.coyote.controller;

import nextstep.FileResolver;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.domain.HttpRequest;

import java.io.IOException;
import java.util.Map;

public class RegisterController extends Controller {

    private static final String GET = "GET";
    private static final String POST = "POST";

    public String run(final HttpRequest request) throws IOException {
        final String parsedUri = request.getUri();
        final String method = request.getMethod();
        if (method.equals(GET)) {
            final FileResolver file = FileResolver.findFile(parsedUri);
            return file.getResponse();
        }
        if (method.equals(POST)) {
            final Map<String, String> body = request.getBody();
            final User user = new User(body.get("account"), body.get("password"), body.get("email"));
            InMemoryUserRepository.save(user);
            final FileResolver file = FileResolver.INDEX_HTML;
            return file.getResponse();
        }
        return null;
    }
}
