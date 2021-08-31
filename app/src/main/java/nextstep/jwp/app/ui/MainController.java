package nextstep.jwp.app.ui;

import nextstep.jwp.mvc.annotation.Controller;
import nextstep.jwp.mvc.annotation.RequestMapping;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.jwp.webserver.response.HttpResponse;

@Controller
public class MainController {

    @RequestMapping(method = HttpMethod.GET, path = "/")
    public String index() {
        return "index.html";
    }
}
