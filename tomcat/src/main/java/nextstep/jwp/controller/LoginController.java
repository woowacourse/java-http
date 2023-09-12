package nextstep.jwp.controller;

import static org.apache.coyote.HttpStatus.FOUND;
import static org.apache.coyote.HttpStatus.OK;
import static org.apache.coyote.header.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.header.HttpHeaders.CONTENT_TYPE;
import static org.apache.coyote.header.HttpHeaders.LOCATION;
import static org.apache.coyote.header.HttpHeaders.SET_COOKIE;

import java.util.Map;
import nextstep.jwp.application.LoginService;
import nextstep.jwp.model.User;
import nextstep.jwp.util.ResourceLoaderUtil;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.header.ContentType;
import org.apache.coyote.header.HttpCookie;
import org.apache.coyote.http11.handler.AbstractController;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

public class LoginController extends AbstractController {

    private final LoginService loginService = new LoginService();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setVersion(request.protocolVersion());
        if (request.getSession() != null) {
            response.setStatus(FOUND);
            response.addHeader(LOCATION, "index.html");
            return;
        }

        String content = ResourceLoaderUtil.loadContent(request.requestUrl());
        response.setStatus(OK);
        response.addHeader(CONTENT_TYPE, ContentType.negotiate(request.requestUrl()));
        response.addHeader(CONTENT_LENGTH, String.valueOf(content.getBytes().length));
        response.setBody(content);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
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
