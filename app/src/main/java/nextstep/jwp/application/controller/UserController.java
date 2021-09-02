package nextstep.jwp.application.controller;

import nextstep.jwp.application.service.UserService;
import nextstep.jwp.framework.http.common.HttpSession;
import nextstep.jwp.framework.http.request.HttpRequest;
import nextstep.jwp.framework.manager.annotation.*;

import java.util.Objects;

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
    public String showLoginPage(final HttpRequest httpRequest) {
        final HttpSession session = httpRequest.getSession();
        if (Objects.isNull(session)) {
            return "/login.html";
        }

        final Object user = session.getAttribute("user");
        if (Objects.isNull(user)) {
            return "/login.html";
        }

        return "redirect:/index.html";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam(value = "account") final String account,
                            @RequestParam(value = "password") final String password,
                            final HttpRequest httpRequest) {
        final boolean isUser = userService.loginUser(account, password, httpRequest);

        if (isUser) {
            return "redirect:/index.html";
        }
        return "redirect:/401.html";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "/register.html";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam(value = "account") final String account,
                               @RequestParam(value = "password") final String password,
                               @RequestParam(value = "email") final String email) {
        userService.register(account, password, email);
        return "redirect:/index.html";
    }
}
