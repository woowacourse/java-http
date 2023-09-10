package nextstep.jwp.controller;

import static org.apache.coyote.HttpStatus.FOUND;
import static org.apache.coyote.header.HttpHeaders.LOCATION;
import static org.apache.coyote.header.HttpHeaders.SET_COOKIE;
import static org.apache.coyote.header.HttpMethod.POST;

import java.util.Map;
import nextstep.jwp.application.LoginService;
import nextstep.jwp.model.User;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.header.HttpCookie;
import org.apache.coyote.http11.handler.Controller;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

public class LoginController implements Controller {

    private final LoginService loginService = new LoginService();

    @Override
    public boolean support(HttpRequest request) {
        return request.httpMethod().equals(POST) && request.requestUrl().startsWith("/login");
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        Map<String, String> requestBody = request.getRequestBody();

        response.setVersion(request.protocolVersion());
        response.setStatus(FOUND);
        try {
            User user = loginService.login(requestBody.get("account"), requestBody.get("password"));
            response.addHeader(LOCATION, "index.html");

            Session session = new Session();
            session.setAttribute(user.getAccount(), user);
            SessionManager.add(session);
            HttpCookie cookie = new HttpCookie();
            cookie.addSessionId(session.id());
            response.addHeader(SET_COOKIE, cookie.toString());
        } catch (RuntimeException e) {
            response.addHeader(LOCATION, "401.html");
        }
    }
}
