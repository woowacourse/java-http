package nextstep.jwp.controller;

import nextstep.jwp.servlet.handler.RequestMapping;
import org.apache.coyote.support.HttpMethod;

public class HomeController {

    @RequestMapping(method = HttpMethod.GET, path = {"/", "/index"})
    public String getHomePage() {
        return "/index.html";
    }
}
