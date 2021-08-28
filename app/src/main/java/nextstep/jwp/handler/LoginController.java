package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.request.RequestUriPath;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.view.View;

import java.util.Map;

public class LoginController {

    public View mapping(RequestLine requestLine) {
        RequestUriPath uriPath = requestLine.getUriPath();
        String method = requestLine.getMethod();

        if (uriPath.getPath().equalsIgnoreCase("/login") && method.equalsIgnoreCase("get")) {
            return login(uriPath.getParams());
        }
        return View.empty();
    }

    public View login(Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);
        user.checkPassword(password);
        return View.of("login.html");
    }
}
