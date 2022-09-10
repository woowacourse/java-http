package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.exception.LoginFailException;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> requestBody = request.getRequestBody();
        UserService userService = new UserService();

        try {
            userService.register(requestBody);
            response.forward(INDEX_REDIRECT_PAGE);
        } catch (LoginFailException e) {
            response.forward(ERROR_REDIRECT_PAGE);
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        response.forward(path);
    }
}
