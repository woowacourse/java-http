package nextstep.jwp.app.ui;

import nextstep.jwp.core.annotation.Controller;
import nextstep.jwp.core.handler.mapping.RequestMapping;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.request.HttpMethod;
import nextstep.jwp.response.HttpResponse;

@Controller
public class LoginController {

    @RequestMapping(method = HttpMethod.GET, path = "/")
    public String index(HttpRequest httpRequest, HttpResponse httpResponse) {
        return "index.html";
    }

    @RequestMapping(method = HttpMethod.GET, path = "/login")
    public String loginPage(HttpRequest httpRequest, HttpResponse httpResponse) {
        return "login.html";
    }
}
