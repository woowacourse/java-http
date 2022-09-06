package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.service.LoginService;
import org.apache.catalina.servlets.AbstractController;
import org.apache.coyote.http11.Resource;
import org.apache.coyote.http11.ResourceLocator;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.spec.HttpStatus;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionStorage;

public class LoginController extends AbstractController {

    public LoginController(ResourceLocator resourceLocator) {
        super(resourceLocator);
    }

    @Override
    public boolean isProcessable(HttpRequest request) {
        return request.isPathEqualTo("/login");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {

        if (SessionStorage.hasSession(request)) {
            response.setStatus(HttpStatus.FOUND);
            response.addHeader("Location", "/index.html");
            return;
        }

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

        LoginService loginService = new LoginService();
        if (!loginService.login(account, password)) {
            Resource resource = resourceLocator.locate("/401.html");
            response.setStatus(HttpStatus.OK);
            response.addHeader("Content-Type", resource.getContentType().getValue());
            response.setBody(resource.getData());
            return;
        }

        Session session = SessionStorage.createSession(request, response);
        session.setAttribute("user", loginService.findUserByAccount(account));
        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
    }
}
