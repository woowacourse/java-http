package nextstep.jwp.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.RequestUriPath;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;

public class LoginController implements IController {

    @Override
    public boolean mapping(String method, RequestUriPath uriPath) {
        if (uriPath.getPath().equalsIgnoreCase("/login") && method.equalsIgnoreCase("get")) {
            return true;
        }
        return false;
    }

    @Override
    public ModelAndView service(String method, RequestUriPath uriPath) {
        if (uriPath.getPath().equalsIgnoreCase("/login") && method.equalsIgnoreCase("get")) {
            return login(uriPath.getParams());
        }
        throw new IllegalArgumentException("핸들러가 처리할 수 있는 요청이 아닙니다.");
    }

    public ModelAndView login(Map<String, String> params) {
        if (params.isEmpty()) {
            return new ModelAndView(Model.of(HttpStatus.OK), "/login.html");
        }
        if (isValidUser(params.get("account"), params.get("password"))) {
            return new ModelAndView(Model.of(HttpStatus.FOUND, "index.html"));
        }
        return new ModelAndView(Model.of(HttpStatus.UNAUTHORIZED), "/401.html");
    }

    private boolean isValidUser(String account, String password) {
        try {
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
            return user.checkPassword(password);
        } catch (Exception e) {
            return false;
        }
    }
}
