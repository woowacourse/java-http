package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.model.User;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.utils.FileConverter;

public class LoginController extends AbstractController {

    private final LoginService loginService = new LoginService();

    @Override
    protected void doGet(Request request, Response response) throws IOException {
        String responseBody = FileConverter.fileToString("/login.html");
        response.set200OK(request, responseBody);
    }

    @Override
    protected void doPost(Request request, Response response) {
        String account = request.getRequestBody("account");
        String password = request.getRequestBody("password");
        User user = new User(0L, account, password, "");
        loginService.loginValidate(user);
        response.set302Found("/index.html");
    }
}
