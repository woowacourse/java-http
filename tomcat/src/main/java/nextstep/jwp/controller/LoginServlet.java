package nextstep.jwp.controller;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoSuchUserException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
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
        Session session = httpRequest.getSession();

        User user = (User) session.getAttribute("user");
        if (Objects.nonNull(user)) {
            httpResponse.sendRedirect("/index.html");
            return;
        }

        httpResponse.addStatusCode(StatusCode.OK);
        httpResponse.addView("login.html");
    }

    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        String account = httpRequest.getRequestParameter("account");
        String password = httpRequest.getRequestParameter("password");

        if (InMemoryUserRepository.existsAccountAndPassword(account, password)) {
            login(httpRequest, httpResponse, account);
            return;
        }

        httpResponse.addStatusCode(StatusCode.UNAUTHORIZED);
        httpResponse.addView("401.html");
    }

    private void login(final HttpRequest httpRequest, final HttpResponse httpResponse, final String account) {
        User user = InMemoryUserRepository.findByAccount(account).orElseThrow(NoSuchUserException::new);
        log.info(user.toString());

        Session session = httpRequest.getSession();
        session.setAttribute("user", user);

        httpResponse.addCookie(session.parseJSessionId());
        httpResponse.sendRedirect("/index.html");
    }
}
