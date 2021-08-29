package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryParams;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.request.RequestUriPath;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;

public class LoginController implements Controller {

    @Override
    public boolean mapping(RequestLine requestLine) {
        String method = requestLine.getMethod();
        RequestUriPath uriPath = requestLine.getUriPath();
        return method.equalsIgnoreCase("get") && uriPath.isPath("/login");
    }

    // TODO :: service controller 분리

    @Override
    public ModelAndView service(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();
        String method = requestLine.getMethod();
        RequestUriPath uriPath = requestLine.getUriPath();

        if (method.equalsIgnoreCase("get") && uriPath.isPath("/login")) {
            return login(uriPath.getQueryParams());
        }
        throw new IllegalArgumentException("핸들러가 처리할 수 있는 요청이 아닙니다.");
    }

    private ModelAndView login(QueryParams params) {
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
