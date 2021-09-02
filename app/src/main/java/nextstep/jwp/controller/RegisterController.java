package nextstep.jwp.controller;

import nextstep.jwp.UserService;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        UserService userService = new UserService();
        userService.saveUser(request.getBody());
        return createRedirectResponse(request, "/index.html");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return create200Response(request);
    }
}
