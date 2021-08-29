package nextstep.jwp.application.controller;

import nextstep.jwp.application.service.UserService;
import nextstep.jwp.manager.annotation.Controller;
import nextstep.jwp.manager.annotation.GetMapping;
import nextstep.jwp.manager.annotation.PostMapping;
import nextstep.jwp.request.QueryParam;

@Controller
public class UserController {

    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    @GetMapping("/login")
    public String showLoginPage(QueryParam queryParam) {
        return "/login.html";
    }

    @PostMapping("/login")
    public String loginUser(QueryParam queryParam) {
        final String account = queryParam.searchValue("account");
        final String password = queryParam.searchValue("password");

        final boolean isUser = userService.loginUser(account, password);
        if (isUser) {
            return "redirect: /index.html";
        }
        return "redirect: /401.html";
    }

    @GetMapping("/register")
    public String showRegisterPage(QueryParam queryParam) {
        return "/register.html";
    }

    @PostMapping("/register")
    public String registerUser(QueryParam queryParam) {
        final String account = queryParam.searchValue("account");
        final String password = queryParam.searchValue("password");
        final String email = queryParam.searchValue("email");

        userService.register(account, password, email);
        return "redirect: /index.html";
    }
}
