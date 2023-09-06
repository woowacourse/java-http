package org.apache.coyote.controller;

import nextstep.FileResolver;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.domain.HttpRequest;
import org.apache.coyote.http11.domain.HttpRequestBody;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
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
            final HttpRequestBody body = request.getBody();
            final User user = new User(body.getAccount(), body.getPassword(), body.getEmail());
            InMemoryUserRepository.save(user);
            final FileResolver file = FileResolver.INDEX_HTML;
            return file.getResponse();
        }
        return null;
    }

    private Map<String, String> parseQueryStrings(final String parsedUri) {
        if (!parsedUri.contains("?")) {
            return Collections.emptyMap();
        }
        final int index = parsedUri.indexOf("?");
        final String queryStringUri = parsedUri.substring(index + 1);
        final String[] strings = queryStringUri.split("&");
        Map<String, String> queryStrings = new HashMap<>();
        for (final String string : strings) {
            final String[] keyValue = string.split("=");
            queryStrings.put(keyValue[0], keyValue[1]);
        }
        return queryStrings;
    }
}
