package nextstep.jwp.application.controller;

import nextstep.jwp.application.service.UserService;
import nextstep.jwp.framework.manager.annotation.Controller;
import nextstep.jwp.framework.manager.annotation.GetMapping;
import nextstep.jwp.framework.manager.annotation.PostMapping;
import nextstep.jwp.framework.manager.annotation.RequestParameter;

@Controller
public class UserController {

    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    @GetMapping("/")
    public String showDefaultPage() {
        return "/index.html";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "/login.html";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParameter(value = "account") String account,
                            @RequestParameter(value = "password") String password) {
        final boolean isUser = userService.loginUser(account, password);

        if (isUser) {
            return "redirect: /index.html";
        }
        return "redirect: /401.html";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "/register.html";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParameter(value = "account") String account,
                               @RequestParameter(value = "password") String password,
                               @RequestParameter(value = "email") String email) {
        userService.register(account, password, email);
        return "redirect: /index.html";
    }
}
