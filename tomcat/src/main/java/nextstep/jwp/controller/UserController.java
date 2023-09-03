package nextstep.jwp.controller;

import static org.apache.coyote.http11.common.ContentType.HTML;
import static org.apache.coyote.http11.common.Status.FOUND;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
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
            return redirect("/index.html");
        }
        return redirect("/401.html");
    }

    private static Response redirect(String location) {
        Response response = Response.of(FOUND, HTML.toString(), "");
        response.addLocation(location);
        return response;
    }

    public static Response register(Request request) {
        /// TODO: 2023/09/04 request body 형식 확인
        String form = request.getBody();
        Map<String, List<String>> formContents = QueryStringParser.parse(form);
        String account = formContents.get("account").get(0);
        String email = formContents.get("email").get(0);
        String password = formContents.get("password").get(0);

        if (InMemoryUserRepository.isExistByAccount(account)) {
            return redirect("/401.html");
        }
        InMemoryUserRepository.save(new User(account, password, email));

        return redirect("/index.html");
    }
}
