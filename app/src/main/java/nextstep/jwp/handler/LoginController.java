package nextstep.jwp.handler;

import nextstep.jwp.exception.IncorrectHandlerException;
import nextstep.jwp.handler.dto.LoginRequest;
import nextstep.jwp.handler.exception.UnauthorizedException;
import nextstep.jwp.handler.service.LoginService;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryParams;
import nextstep.jwp.http.request.RequestLine;

public class LoginController implements Handler {

    private LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public boolean mapping(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.requestLine();
        return requestLine.isPath("/login");
    }

    @Override
    public ResponseEntity service(HttpRequest httpRequest) {
        if (httpRequest.isGet()) {
            return printLoginPage();
        }
        if (httpRequest.isPost()) {
            return login(QueryParams.of(httpRequest.requestBody()));
        }
        throw new IncorrectHandlerException();
    }

    private ResponseEntity printLoginPage() {
        return ResponseEntity.ok("/login.html");
    }

    private ResponseEntity login(QueryParams params) {
        try{
            loginService.login(LoginRequest.fromQueryParams(params));
            return ResponseEntity.redirect("index.html");
        } catch (UnauthorizedException exception){
            return ResponseEntity.unauthorized();
        }
    }
}
