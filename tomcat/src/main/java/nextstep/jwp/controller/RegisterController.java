package nextstep.jwp.controller;

import nextstep.jwp.service.LoginService;
import org.apache.catalina.servlets.AbstractController;
import org.apache.coyote.http11.ResourceLocator;
import org.apache.coyote.http11.general.ContentType;
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
        if (request.getContentType() != ContentType.APPLICATION_FORM_URLENCODED) {
            response.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            return;
        }

        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        LoginService loginService = new LoginService();
        loginService.register(account, password, email);

        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
    }

}
