package nextstep.jwp.controller;

import nextstep.jwp.servlet.handler.RequestMapping;
import org.apache.coyote.support.HttpMethod;

public class GetController {

    @RequestMapping(method = HttpMethod.GET, path = {"/", "/index"})
    public String home() {
        return "/index.html";
    }

    @RequestMapping(method = HttpMethod.GET, path = "/login")
    public String login() {
        return "/login.html";
    }

    @RequestMapping(method = HttpMethod.GET, path = "/register")
    public String register() {
        return "/register.html";
    }
}
