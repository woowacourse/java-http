package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.service.LoginService;
import org.apache.catalina.servlets.AbstractController;
import org.apache.coyote.http11.Resource;
import org.apache.coyote.http11.ResourceLocator;
import org.apache.coyote.http11.general.HttpHeaders;
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
        HttpHeaders headers = request.getHeaders();
        String contentType = headers.get("Content-Type");
        if (!contentType.equals("application/x-www-form-urlencoded")) {
            response.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            return;
        }
        String body = request.getBody();
        Map<String, String> data = new HashMap<>();
        String[] components = body.split("&");
        for (String component : components) {
            String[] keyVal = component.split("=");
            String key = keyVal[0];
            String value = keyVal[1];
            data.put(key, value);
        }

        String account = data.get("account");
        String password = data.get("password");

        LoginService loginService = new LoginService();
        if (!loginService.login(account, password)) {
            Resource resource = resourceLocator.locate("/401.html");
            response.setStatus(HttpStatus.OK);
            response.addHeader("Content-Type", resource.getMimeType().getValue());
            response.setBody(resource.getData());
            return;
        }

        Session session = SessionStorage.createSession(request, response);
        session.setAttribute("user", loginService.findUserByAccount(account));
        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
    }
}
