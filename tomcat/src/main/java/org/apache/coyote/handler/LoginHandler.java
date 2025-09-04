package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginHandler extends AbstractHandler {

    @Override
    public boolean canHandle(final String requestTarget) {
        return requestTarget.startsWith("/login");
    }

    @Override
    public String handle(final String requestUri) throws IOException {
        final String[] splitRequestUri = requestUri.split("\\?");
        final String resource = getResource(splitRequestUri[0]);
        if (existsQueryString(splitRequestUri)) {
            findMemberByQuery(splitRequestUri[1]);
        }
        final String responseBody = getStaticResponseBody(resource);

        return createOkResponse(responseBody, "text/html;charset=utf-8");
    }

    private String getResource(final String resource) {
        if (!resource.endsWith(".html")) {
            return resource + ".html";
        }

        return resource;
    }

    private boolean existsQueryString(final String[] splitRequestUri) {
        return splitRequestUri.length > 1;
    }

    private void findMemberByQuery(final String queryString) {
        final Map<String, String> params = getParams(queryString);
        final User user = InMemoryUserRepository.findByAccount(params.get("account"))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        validateLogin(user, params.get("password"));
        System.out.println("user: " + user);
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

    private  void validateLogin(
            final User user,
            final String password
    ) {
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}
