package org.apache.catalina.controller;

import java.util.Map;
import java.util.UUID;
import nextstep.jwp.service.UserService;
import nextstep.mvc.ResponseWriter;
import org.apache.catalina.controller.config.RequestMapping;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.util.HttpParser;

@RequestMapping("/login")
public class LoginController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        ResponseWriter.view(httpResponse, HttpStatus.OK, httpRequest.getPath());
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final var body = httpRequest.getBody();
        Map<String, String> parameters = HttpParser.parseFormData(body);
        if (!parameters.containsKey("account") && parameters.containsKey("password")) {
            return;
        }

        boolean login = UserService.login(parameters.get("account"), parameters.get("password"),
                httpRequest.getSession());
        if (login) {
            final var session = new Session(UUID.randomUUID().toString());
            SessionManager.add(session);
            httpResponse.addCookie(new HttpCookie(HttpCookie.JSESSIONID, session.getId()));
            ResponseWriter.redirect(httpResponse, "/index.html");
            return;
        }
        ResponseWriter.redirect(httpResponse, "/401.html");
    }
}
