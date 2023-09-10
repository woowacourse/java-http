package nextstep.jwp.controller;

import static org.apache.coyote.response.Status.FOUND;
import static org.apache.coyote.utils.Constant.SESSION_COOKIE_NAME;
import static org.apache.coyote.utils.Converter.parseFormData;

import java.net.URL;
import java.util.Map;
import nextstep.jwp.service.UserService;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class LoginController extends AbstractController {

    private final UserService userService = new UserService();

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> bodyFields = parseFormData(request.getBody());
        final String sessionId = userService.logIn(bodyFields);

        makeAuthorizedResponse(response, sessionId);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        if (request.isCookieExist(SESSION_COOKIE_NAME)) {
            makeAuthorizedResponse(response);
            return;
        }
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        response.setBody(resource);
    }

    private void makeAuthorizedResponse(final HttpResponse response, final String sessionId) {
        response.setStatus(FOUND);
        response.setRedirectUri("/index.html");
        response.addCookie(SESSION_COOKIE_NAME, sessionId);
    }

    private void makeAuthorizedResponse(final HttpResponse response) {
        response.setStatus(FOUND);
        response.setRedirectUri("/index.html");
    }
}
