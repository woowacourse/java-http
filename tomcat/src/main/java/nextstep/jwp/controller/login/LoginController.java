package nextstep.jwp.controller.login;

import javassist.NotFoundException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.handler.mapper.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.Status;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class LoginController extends AbstractController {

    private static final String USER = "user";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    private final UserService userService;

    public LoginController(final UserService userService) {
        this.userService = userService;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) throws Exception {
        if (httpRequest.getSessionAttribute(USER).isPresent()) {
            return HttpResponse.responseWithResource(Status.FOUND, "/index.html");
        }

        return HttpResponse.okWithResource("/login.html");
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) throws Exception {
        Map<String, String> params = httpRequest.getParams();
        User user = userService.login(params.get(ACCOUNT), params.get(PASSWORD));

        return getHttpResponseWithCookie(httpRequest, user);
    }

    private HttpResponse getHttpResponseWithCookie(final HttpRequest httpRequest, final User user) throws NotFoundException, IOException, URISyntaxException {
        Session session = httpRequest.createSession();
        session.setAttribute(USER, user);

        HttpResponse response = HttpResponse.responseWithResource(Status.FOUND, "/index.html");
        response.setCookie(Cookie.fromUserJSession(session.getId()));

        return response;
    }

}
