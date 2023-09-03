package nextstep.jwp.controller;

import static org.apache.coyote.http11.common.ContentType.HTML;
import static org.apache.coyote.http11.common.Status.FOUND;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.response.Response;

public class UserController {

    private UserController() {
    }

    public static Response login(Map<String, List<String>> queryParams) {
        String account = queryParams.get("account").get(0);
        String password = queryParams.get("password").get(0);

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
}
