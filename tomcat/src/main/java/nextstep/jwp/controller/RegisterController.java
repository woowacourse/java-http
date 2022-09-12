package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.exception.LoginFailException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> requestBody = request.getRequestBody();
        UserService userService = new UserService();
        User user = toUser(requestBody);

        try {
            userService.register(user);
            response.forward(INDEX_REDIRECT_PAGE);
        } catch (LoginFailException e) {
            response.forward(ERROR_REDIRECT_PAGE);
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        response.forward(path);
    }

    private User toUser(Map<String, String> requestBody) {
        String account = requestBody.getOrDefault("account", EMPTY_VALUE);
        String password = requestBody.getOrDefault("password", EMPTY_VALUE);
        String email = requestBody.getOrDefault("email", EMPTY_VALUE);

        return new User(account, password, email);
    }
}
