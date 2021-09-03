package nextstep.jwp.web.ui;

import nextstep.jwp.server.http.request.HttpRequest;
import nextstep.jwp.server.http.response.HttpResponse;
import nextstep.jwp.web.application.RegisterService;

public class RegisterController extends AbstractController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.forward(request.getPath() + ".html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        registerService.register(account, password, email);
        response.sendRedirect("/index.html");
    }
}
