package nextstep.jwp.http;

import static nextstep.jwp.http.HttpResponse.found;
import static nextstep.jwp.http.HttpResponse.redirect;
import static nextstep.jwp.http.HttpResponse.unauthorized;
import static nextstep.jwp.http.ViewResolver.resolveView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class Controller {

    public String login() throws IOException {
        return resolveView("login");
    }

    public String login(String requestBody) {
        Map<String, String> queryMap = bodyMapper(requestBody);

        User user = InMemoryUserRepository.findByAccount(queryMap.get("account")).orElseThrow(
                IllegalArgumentException::new);
        if (!user.checkPassword(queryMap.get("password"))) {
            return unauthorized();
        }
        return found("/index.html");
    }

    public String register() throws IOException {
        return resolveView("register");
    }

    public String register(String requestBody) throws IOException {
        Map<String, String> queryMap = bodyMapper(requestBody);

        User user = new User(2L, queryMap.get("account"), queryMap.get("password"), queryMap.get("email"));

        InMemoryUserRepository.save(user);
        return redirect("/index.html");
    }

    private Map<String, String> bodyMapper(String requestBody) {
        String[] strings = requestBody.split("&");

        Map<String, String> queryMap = new HashMap<>();

        for (String string : strings) {
            String[] token = string.split("=");
            queryMap.put(token[0], token[1]);
        }
        return queryMap;
    }
}
