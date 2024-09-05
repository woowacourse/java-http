package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.QueryParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Optional;

public class LoginHandler extends AbstractHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        URI uri = httpRequest.getUri();
        String path = uri.getPath();

        return "/login".equals(path);
    }

    @Override
    protected String forward(HttpRequest httpRequest) {
        QueryParameter queryParameter = new QueryParameter(httpRequest.getUri().getQuery());
        checkUser(queryParameter);

        return "static/login.html";
    }

    private void checkUser(QueryParameter queryParameter) {
        String password = queryParameter.get("password").orElse("");
        Optional<User> user = queryParameter.get("account").flatMap(InMemoryUserRepository::findByAccount);

        if (user.isPresent()) {
            boolean isSame = user.get().checkPassword(password);
            if (isSame) {
                log.info("{}", user.get());
            }
        }
    }
}
