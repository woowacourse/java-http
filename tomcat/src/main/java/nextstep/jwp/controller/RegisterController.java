package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.service.LoginService;
import org.apache.catalina.servlets.AbstractController;
import org.apache.coyote.http11.ResourceLocator;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.spec.HttpStatus;

public class RegisterController extends AbstractController {

    public RegisterController(ResourceLocator resourceLocator) {
        super(resourceLocator);
    }

    @Override
    public boolean isProcessable(HttpRequest request) {
        return request.isPathEqualTo("/register");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String path = request.getPathString() + ".html";
        doHtmlResponse(response, path);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> data = parseFormPayload(request, response);
        if (data == null) {
            return;
        }

        String account = data.get("account");
        String password = data.get("password");
        String email = data.get("email");

        LoginService loginService = new LoginService();
        loginService.register(account, password, email);

        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
    }

}
