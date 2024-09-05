package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.QueryParameter;

import java.net.URI;

public class PostLoginHandler extends AbstractHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        URI uri = httpRequest.getUri();
        String path = uri.getPath();

        return "/login".equals(path) && httpRequest.getMethod().isPost();
    }

    @Override
    protected String forward(HttpRequest httpRequest) {
        QueryParameter queryParameter = httpRequest.body();
        if (isLoggedIn(queryParameter)) {
            return "redirect:index.html";
        }

        return "redirect:401.html";
    }

    private boolean isLoggedIn(QueryParameter queryParameter) {
        String password = queryParameter.get("password").orElse("");

        return queryParameter.get("account")
                .flatMap(InMemoryUserRepository::findByAccount)
                .map(it -> it.checkPassword(password))
                .orElse(false);
    }
}
