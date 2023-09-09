package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FrontController extends AbstractController {

    private static final Map<String, Controller> URIS = new HashMap<>();

    static {
        URIS.put("/", new HomeController());
        URIS.put("/login", new LoginController());
        URIS.put("/register", new RegisterController());
        URIS.put("/index", new IndexController());
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String uri = request.getRequestLine().getRequestURI().getUri();
        Controller controller = URIS.getOrDefault(uri, new ResourceController());
        controller.service(request, response);
    }

    protected void doPost(HttpRequest request, HttpResponse response) { /* NOOP */ }

    protected void doGet(HttpRequest request, HttpResponse response) { /* NOOP */ }

}
