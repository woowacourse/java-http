package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.model.User;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.utils.FileConverter;

public class RegisterController extends AbstractController {

    private static final RegisterService REGISTER_SERVICE = new RegisterService();

    @Override
    protected void doGet(Request request, Response response) throws IOException {
        String responseBody = FileConverter.fileToString("/register.html");
        response.set200OK(request, responseBody);
    }

    @Override
    protected void doPost(Request request, Response response) {
        String account = request.getRequestBody("account");
        String password = request.getRequestBody("password");
        String email = request.getRequestBody("email");
        User user = new User(0, account, password, email);
        REGISTER_SERVICE.save(user);
        response.set302Found("/index.html");
    }
}
