package nextstep.jwp.controller;

import java.util.NoSuchElementException;
import nextstep.jwp.dto.LoginResponseDto;
import nextstep.jwp.exception.InvalidLoginInfoException;
import nextstep.jwp.service.LoginService;

public class LoginController {

    private final LoginService loginService = new LoginService();

    public LoginResponseDto login(String account, String password) {
        try {
            loginService.login(account, password);
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
