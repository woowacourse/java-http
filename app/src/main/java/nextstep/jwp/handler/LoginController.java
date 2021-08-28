package nextstep.jwp.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.request.RequestUriPath;
import nextstep.jwp.model.User;
import nextstep.jwp.view.View;

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
        if (params.isEmpty()) {
            return View.of("login.html");
        }

        String account = params.get("account");
        String password = params.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);

        if (user.checkPassword(password)) {
            return View.of(user.toString());
        }

        throw new IllegalArgumentException("존재하지 않는 회원 정보입니다.");
    }
}
