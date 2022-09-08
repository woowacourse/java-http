package nextstep.jwp.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.exception.AccountNotFoundException;
import org.apache.coyote.AbstractController;
import org.apache.coyote.Session;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Override
    protected void doPost(final Request request, final Response response) throws Exception {
        runLogin(request, response);
    }

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        if (!request.hasJsessionid()) {
            response.write(HttpStatus.OK, "/login.html");
            return;
        }
        response.addHeader("Location", "/index.html");
        response.write(HttpStatus.FOUND);
    }

    private void runLogin(final Request request, final Response response)
            throws IOException, URISyntaxException {
        final RequestBody body = request.getBody();
        if (loginSuccess(body)) {
            final User loggedInUser = findUser(body);
            addToSession(response, loggedInUser);
            return;
        }
        response.addHeader("Location", "/401.html");
        response.write(HttpStatus.FOUND);
    }

    private void addToSession(final Response response, final User loggedInUser)
            throws IOException, URISyntaxException {
        final String jsessionid = UUID.randomUUID().toString();
        Session.add(jsessionid, "user", loggedInUser);
        response.addHeader("Set-Cookie", "JSESSIONID="+jsessionid);
        log.info(loggedInUser.toString());
        response.addHeader("Location", "/index.html");
        response.write(HttpStatus.FOUND);
    }

    private boolean loginSuccess(final RequestBody body) {
        final User foundUser = findUser(body);
        final String password = body.get("password");
        return foundUser.checkPassword(password);

    }

    private User findUser(final RequestBody body) {
        String account = body.get("account");
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(AccountNotFoundException::new);
    }
}
