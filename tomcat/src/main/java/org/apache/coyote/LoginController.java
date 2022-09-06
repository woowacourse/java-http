package org.apache.coyote;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.exception.AccountNotFoundException;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11RequestBody;
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
        if (request.getMethod().equals(HttpMethod.POST)) {
            runLogin(request, response);
        }
        response.write(HttpStatus.OK, new Http11URL("/login.html"));
    }

    private void runLogin(final Http11Request request, final Http11Response response)
            throws IOException, URISyntaxException {
        final Http11RequestBody body = request.getBody();
        if (loginSuccess(body)) {
            final User loggedInUser = findUser(body);
            log.info(loggedInUser.toString());
        }
        response.write(HttpStatus.FOUND, new Http11URL("/index.html"));
    }

    private boolean loginSuccess(final Http11RequestBody body) {
        final User foundUser = findUser(body);
        final String password = body.get("password");
        return foundUser.checkPassword(password);

    }

    private User findUser(final Http11RequestBody body) {
        String account = body.get("account");
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(AccountNotFoundException::new);
    }
}
