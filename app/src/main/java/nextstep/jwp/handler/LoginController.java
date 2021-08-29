package nextstep.jwp.handler;

import nextstep.jwp.handler.dto.LoginRequest;
import nextstep.jwp.handler.service.LoginService;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryParams;
import nextstep.jwp.http.request.RequestLine;

public class LoginController implements Controller {

    private LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public boolean mapping(RequestLine requestLine) {
        return requestLine.isFrom("get", "/login") || requestLine.isFrom("post", "/login");
    }

    @Override
    public ModelAndView service(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();
        if (requestLine.isFrom("get", "/login")) {
            return printLoginPage();
        }
        if (requestLine.isFrom("post", "/login")) {
            return login(httpRequest.getRequestBody());
        }
        throw new IllegalArgumentException("핸들러가 처리할 수 있는 요청이 아닙니다.");
    }

    private ModelAndView printLoginPage() {
        return ModelAndView.ok("/login.html");
    }

    private ModelAndView login(String requestBody) {
        try {
            QueryParams params = QueryParams.of(requestBody);
            LoginRequest loginRequest = new LoginRequest(params.get("account"), params.get("password"));
            loginService.login(loginRequest);
            return ModelAndView.redirect("/index.html");
        } catch (Exception e) {
            return ModelAndView.unauthorized();
        }
    }
}
