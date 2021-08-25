package nextstep.common;

import nextstep.jwp.framework.domain.annotation.Controller;
import nextstep.jwp.framework.domain.annotation.GetMapping;

@Controller
public class FixtureController1 {

    @GetMapping("/")
    public String moveToIndexPage() {
        return "/index.html";
    }

    @GetMapping("/login")
    public String moveToLoginPage() {
        return "/login.html";
    }

    @GetMapping("/register")
    public String moveToRegisterPage() {
        return "/register.html";
    }
}
