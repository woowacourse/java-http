package org.apache.coyote;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11QueryParams;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Override
    public boolean isRunnable(final Http11Request request) {
        return request.hasPath("/login");
    }

    @Override
    public void run(final Http11Request request, final Http11Response response) throws IOException, URISyntaxException {
        final Http11QueryParams params = request.getParams();
        if (loginSuccess(params)) {
            final User loggedInUser = findUser(params);
            log.info(loggedInUser.toString());
        }
        response.write(HttpStatus.OK, new Http11URL("/login.html"));
    }

    private boolean loginSuccess(final Http11QueryParams params) {
        if (!params.isEmpty()) {
            return false;
        }
        final User foundUser = findUser(params);
        final String password = params.get("password");
        return foundUser.checkPassword(password);

    }

    private User findUser(final Http11QueryParams params) {
        String account = params.get("account");
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);
    }
}
