package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.apache.servlet.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(RegisterServlet.class);
    private static final String PATH = "/register";

    @Override
    public boolean isSupported(final String path) {
        return PATH.equals(path);
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.addStatusCode(StatusCode.OK);
        httpResponse.addView("register.html");
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (!httpRequest.hasBodyParameter("account", "password", "email")) {
            httpResponse.addStatusCode(StatusCode.OK);
            httpResponse.addView("login.html");
        }

        String account = httpRequest.getBodyParameter("account");
        String password = httpRequest.getBodyParameter("password");
        String email = httpRequest.getBodyParameter("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("{} 회원가입 성공!", user.getAccount());

        httpResponse.sendRedirect("/index.html");
    }
}
