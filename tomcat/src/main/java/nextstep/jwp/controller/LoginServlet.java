package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoSuchUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.apache.servlet.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);
    private static final String PATH = "/login";

    @Override
    public boolean isSupported(final String path) {
        return PATH.equals(path);
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (!httpRequest.hasQueryParameter("account", "password")) {
            httpResponse.sendRedirect("/index.html");
            return;
        }

        validateExistsUser(httpRequest.getQueryParameter("account"), httpRequest.getQueryParameter("password"));
        httpResponse.addStatusCode(StatusCode.OK);
        httpResponse.addView("login.html");
    }

    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        throw new UnsupportedOperationException();
    }

    private void validateExistsUser(final String account, final String password) {
        if (!InMemoryUserRepository.existsAccountAndPassword(account, password)) {
            throw new NoSuchUserException();
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchUserException::new);
        log.info(user.toString());
    }
}
