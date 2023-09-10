package nextstep.jwp.controller;

import static org.apache.catalina.core.servlet.HttpServletResponse.redirect;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.core.servlet.HttpServletResponse;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.QueryParameters;
import org.apache.coyote.http11.request.Request;

/// TODO: 2023/09/10 http11 의존하지 않게 만들기
public class UserController implements Controller {

    private UserController() {
    }

    public static HttpServletResponse login(final Request request) {
        final var form = QueryParameters.from(request.getBody());
        final var account = form.findSingleByKey("account");
        final var password = form.findSingleByKey("password");

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> redirectLoginUser(request, user))
                .orElseGet(() -> redirect("/401.html"));
    }

    private static HttpServletResponse redirectLoginUser(final Request request, final User user) {
        final var session = request.getSession();
        session.setAttribute("user", user);

        return redirect("/index.html")
                .addSetCookie(Cookies.ofJSessionId(session.getId()));
    }

    public static HttpServletResponse register(final Request request) {
        final var form = QueryParameters.from(request.getBody());
        final var account = form.findSingleByKey("account");
        final var email = form.findSingleByKey("email");
        final var password = form.findSingleByKey("password");

        if (InMemoryUserRepository.isExistByAccount(account)) {
            return redirect("/401.html");
        }
        InMemoryUserRepository.save(new User(account, password, email));

        return redirect("/index.html");
    }

}
