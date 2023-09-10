package nextstep.jwp.controller;

import static org.apache.catalina.core.servlet.HttpServletResponse.redirect;
import static org.apache.coyote.http11.session.SessionManager.SESSION_ID_COOKIE_NAME;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.core.servlet.HttpServletRequest;
import org.apache.catalina.core.servlet.HttpServletResponse;

public class UserController implements Controller {

    private UserController() {
    }

    public static HttpServletResponse login(final HttpServletRequest request) {
        final var account = request.getQueryParamsForBody("account");
        final var password = request.getQueryParamsForBody("password");

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> redirectLoginUser(request, user))
                .orElseGet(() -> redirect("/401.html"));
    }

    private static HttpServletResponse redirectLoginUser(final HttpServletRequest request, final User user) {
        final var session = request.getSession();
        session.setAttribute("user", user);

        return redirect("/index.html")
                .addSetCookie(SESSION_ID_COOKIE_NAME, session.getId());
    }

    public static HttpServletResponse register(final HttpServletRequest request) {
        final var account = request.getQueryParamsForBody("account");
        final var email = request.getQueryParamsForBody("email");
        final var password = request.getQueryParamsForBody("password");

        if (InMemoryUserRepository.isExistByAccount(account)) {
            return redirect("/401.html");
        }
        InMemoryUserRepository.save(new User(account, password, email));

        return redirect("/index.html");
    }

}
