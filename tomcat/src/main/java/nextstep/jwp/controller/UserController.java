package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.util.QueryStringParser;

public class UserController {

    private UserController() {
    }

    public static Response login(final Request request) {
        final var form = request.getBody();
        final var formContents = QueryStringParser.parse(form);
        final var account = formContents.get("account").get(0);
        final var password = formContents.get("password").get(0);

        final var user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            final var redirect = Response.redirect("/index.html");
            final var session = request.getOrCreateSession();

            session.setAttribute("user", user.get());
            redirect.addSetCookie(Cookies.ofJSessionId(session.getId()));
            return redirect;
        }

        return Response.redirect("/401.html");
    }

    public static Response register(final Request request) {
        final var form = request.getBody();
        final var formContents = QueryStringParser.parse(form);
        final var account = formContents.get("account").get(0);
        final var email = formContents.get("email").get(0);
        final var password = formContents.get("password").get(0);

        if (InMemoryUserRepository.isExistByAccount(account)) {
            return Response.redirect("/401.html");
        }
        InMemoryUserRepository.save(new User(account, password, email));

        return Response.redirect("/index.html");
    }
}
