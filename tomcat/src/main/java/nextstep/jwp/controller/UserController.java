package nextstep.jwp.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.util.QueryStringParser;

public class UserController {

    private UserController() {
    }

    public static Response login(final Request request) {
        final String form = request.getBody();
        final Map<String, List<String>> formContents = QueryStringParser.parse(form);
        final String account = formContents.get("account").get(0);
        final String password = formContents.get("password").get(0);

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            final Response redirect = Response.redirect("/index.html");
            final Session session = request.getOrCreateSession();
            session.setAttribute("user", user.get());
            redirect.addSetCookie(Cookies.ofJSessionId(session.getId()));
            return redirect;
        }
        return Response.redirect("/401.html");
    }

    public static Response register(final Request request) {
        final String form = request.getBody();
        final Map<String, List<String>> formContents = QueryStringParser.parse(form);
        final String account = formContents.get("account").get(0);
        final String email = formContents.get("email").get(0);
        final String password = formContents.get("password").get(0);

        if (InMemoryUserRepository.isExistByAccount(account)) {
            return Response.redirect("/401.html");
        }
        InMemoryUserRepository.save(new User(account, password, email));

        return Response.redirect("/index.html");
    }
}
