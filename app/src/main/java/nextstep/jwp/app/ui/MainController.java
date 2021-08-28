package nextstep.jwp.app.ui;

import nextstep.jwp.core.annotation.Controller;
import nextstep.jwp.core.mvc.mapping.RequestMapping;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.jwp.webserver.response.HttpResponse;

@Controller
public class MainController {

    @RequestMapping(method = HttpMethod.GET, path = "/")
    public String index(HttpRequest httpRequest, HttpResponse httpResponse) {
        return "index.html";
    }
}
