package nextstep.jwp.controller;

import java.util.NoSuchElementException;
import nextstep.jwp.dto.LoginResponseDto;
import nextstep.jwp.exception.InvalidLoginInfoException;
import nextstep.jwp.service.LoginService;
import nextstep.org.apache.coyote.http11.Cookies;
import nextstep.org.apache.coyote.http11.Session;

public class LoginController {

    private final LoginService loginService = new LoginService();

    public LoginResponseDto login(Cookies cookies, String account, String password) {
        try {
            Session loginSession = loginService.login(account, password);
            cookies.set("JSESSIONID", loginSession.getId());
            return new LoginResponseDto("/index.html");
        } catch (NoSuchElementException | InvalidLoginInfoException | NullPointerException e) {
            return new LoginResponseDto("/401.html");
        }
    }

    public LoginResponseDto register(String account, String password, String email) {
        loginService.register(account, password, email);
        return new LoginResponseDto("/index.html");
    }
}
