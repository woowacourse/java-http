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
    public String handle(final String requestUri) throws IOException {
        final String[] splitRequestUri = requestUri.split("\\?");
        final String resource = splitRequestUri[0];
        if (existsQueryString(splitRequestUri)) {
            findMemberByQuery(splitRequestUri[1]);
        }
        final String responseBody = getStaticResponseBody(resource + ".html");


        return createOkResponse(responseBody, "text/html;charset=utf-8");
    }

    private boolean existsQueryString(final String[] splitRequestUri) {
        return splitRequestUri.length > 1;
    }

    private void findMemberByQuery(final String queryString) {
        final Map<String, String> params = getParams(queryString);
        final Optional<User> user = InMemoryUserRepository.findByAccount(params.get("account"));
        user.ifPresent(value -> System.out.println("user: " + value));
    }

    private Map<String, String> getParams(final String queryString) {
        final Map<String, String> params = new HashMap<>();
        final String[] queries = queryString.split("&");
        for (String query : queries) {
            final String[] pair = query.split("=");
            params.put(pair[0], pair[1]);
        }

        return params;
    }
}
