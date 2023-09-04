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

    public static Response login(Request request) {
        String form = request.getBody();
        Map<String, List<String>> formContents = QueryStringParser.parse(form);
        String account = formContents.get("account").get(0);
        String password = formContents.get("password").get(0);

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            Response redirect = Response.redirect("/index.html");
            Session session = request.getOrCreateSession();
            session.setAttribute("user", user.get());
            redirect.addSetCookie(Cookies.ofJSessionId(session.getId()));
            return redirect;
        }
        return Response.redirect("/401.html");
    }

    public static Response register(Request request) {
        /// TODO: 2023/09/04 request body 형식 확인
        String form = request.getBody();
        Map<String, List<String>> formContents = QueryStringParser.parse(form);
        String account = formContents.get("account").get(0);
        String email = formContents.get("email").get(0);
        String password = formContents.get("password").get(0);

        if (InMemoryUserRepository.isExistByAccount(account)) {
            return Response.redirect("/401.html");
        }
        InMemoryUserRepository.save(new User(account, password, email));

        return Response.redirect("/index.html");
    }
}
