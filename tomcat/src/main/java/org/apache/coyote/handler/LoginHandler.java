package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoginHandler extends AbstractHandler {

    @Override
    public boolean canHandle(final String requestTarget) {
        return requestTarget.startsWith("/login");
    }

    @Override
    public String handle(final String requestTarget) throws IOException {
        final String[] split = requestTarget.split("\\?");
        final String resource = split[0];
        final String queryString = split[1];
        final Map<String, String> params = getParams(queryString);

        final String responseBody = getStaticResponseBody(resource + ".html");

        final Optional<User> user = InMemoryUserRepository.findByAccount(params.get("account"));
        user.ifPresent(value -> System.out.println("user: " + value));

        return createResponse(responseBody, "text/html;charset=utf-8");
    }

    private Map<String, String> getParams(final String queryString) {
        Map<String, String> params = new HashMap<>();
        final String[] queries = queryString.split("&");
        for (String query : queries) {
            final String[] pair = query.split("=");
            params.put(pair[0], pair[1]);
        }

        return params;
    }
}
