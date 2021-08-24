package nextstep.jwp.web.presentation;

import nextstep.jwp.framework.domain.annotation.Controller;
import nextstep.jwp.framework.domain.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String moveToIndexPage() {
        return "static/index.html";
    }

    @GetMapping("/login")
    public String moveToLoginPage() {
        return "static/login.html";
    }

    @GetMapping("/register")
    public String moveToRegisterPage() {
        return "static/register.html";
    }
}
