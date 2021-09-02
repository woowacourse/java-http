package nextstep.jwp.controller;

import java.util.Optional;
import nextstep.jwp.UserService;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {
    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        UserService userService = new UserService();
        String requestBody = request.getBody();
        Optional<User> user = userService.findUserFromBody(requestBody);
        if (user.isEmpty()) {
            return create302Response(request, "/401.html");
        }
        return create302Response(request, "/index.html");
    }


    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return create302Response(request, "/login.html");
    }


}
