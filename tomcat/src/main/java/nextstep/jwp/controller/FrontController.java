package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FrontController implements Controller {

    private static final Map<String, Controller> URIS = new HashMap<>();

    static {
        URIS.put("/", new HomeController());
        URIS.put("/login", new LoginController());
        URIS.put("/register", new RegisterController());
        URIS.put("/index", new IndexController());
    }

    @Override
    public HttpResponse service(HttpRequest request) {
        String uri = request.getRequestLine().getRequestURI().getUri();
        Controller controller = URIS.getOrDefault(uri, new ResourceController());
        return controller.service(request);
    }

}
