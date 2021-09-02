package nextstep.jwp.controller;

import java.util.Optional;
import nextstep.jwp.UserService;
import nextstep.jwp.http.auth.HttpSession;
import nextstep.jwp.http.auth.HttpSessions;
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
            return createRedirectResponse(request, "/401.html");
        }
        HttpSession session = HttpSessions.createSession();
        session.setAttribute("user", user);
        return createSessionRedirectResponse(session, "/index.html");
    }


    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        if (loginUserExistsSession(request)) {
            return createRedirectResponse(request, "/index.html");
        }
        return createRedirectResponse(request, "/login.html");
    }

    private boolean loginUserExistsSession(HttpRequest request) {
        return HttpSessions.getSession(request.getCookie()) != null;
    }

}
