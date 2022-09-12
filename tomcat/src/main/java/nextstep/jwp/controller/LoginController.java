package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.exception.LoginFailException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.util.UUIDGenerator;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> requestBody = request.getRequestBody();
        HttpCookie httpCookie = request.getCookies();
        UserService userService = new UserService();

        try {
            userService.login(requestBody);
            String identifier = UUIDGenerator.generate();
            httpCookie.addJSessionId(identifier);

            Session session = request.getSession();
            User user = userService.findUser(requestBody);
            session.setAttribute("user", user);
            sessionManager.add(session);

            response.addCookie(httpCookie);
            response.sendRedirect(INDEX_REDIRECT_PAGE);
        } catch (LoginFailException e) {
            response.sendRedirect(ERROR_REDIRECT_PAGE);
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        HttpCookie httpCookie = request.getCookies();
        String jSessionId = httpCookie.getJSessionId();
        Session session = sessionManager.findSession(jSessionId)
                .orElseGet(() -> new Session(EMPTY_VALUE));

        if (!session.getId().isBlank()) {
            UserService userService = new UserService();
            User user = userService.getUser(session);
            if (userService.isValidUser(user)) {
                response.sendRedirect(INDEX_REDIRECT_PAGE);
                return;
            }
        }

        String path = request.getPath();
        response.forward(path);
    }
}
