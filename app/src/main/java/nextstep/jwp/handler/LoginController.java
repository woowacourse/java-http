package nextstep.jwp.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.request.RequestUriPath;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;

public class LoginController {

    public ModelAndView mapping(RequestLine requestLine) {
        RequestUriPath uriPath = requestLine.getUriPath();
        String method = requestLine.getMethod();

        if (uriPath.getPath().equalsIgnoreCase("/login") && method.equalsIgnoreCase("get")) {
            return login(uriPath.getParams());
        }
        return null;
    }

    public ModelAndView login(Map<String, String> params) {
        if (params.isEmpty()) {
            return new ModelAndView(Model.of(HttpStatus.OK), "login.html");
        }

        String account = params.get("account");
        String password = params.get("password");

        // TODO :: 없는 사용자 예외 처리
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(IllegalArgumentException::new);

        if (user.checkPassword(password)) {
            return new ModelAndView(Model.of(HttpStatus.FOUND, "index.html"));
        }

        return new ModelAndView(Model.of(HttpStatus.UNAUTHORIZED), "401.html");
    }
}
