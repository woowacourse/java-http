package org.apache.coyote.controller;

import com.techcourse.model.User;
import com.techcourse.service.SessionService;
import com.techcourse.service.UserService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.apache.util.ResourceReader;
import org.apache.util.parser.BodyParserFactory;
import org.apache.util.parser.Parser;

public class LoginController extends FrontController {

    private final UserService userService = UserService.getInstance();
    private final SessionService sessionService = SessionService.getInstance();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException, URISyntaxException {
        String body = request.getBody();
        Parser parser = BodyParserFactory.getParser(ContentType.FORM_DATA);

        Map<String, String> params = parser.parse(body);
        String account = params.get("account");
        String password = params.get("password");

        if (!userService.isAccountExist(account)) {
            handleFailedLogin(response);
            return;
        }

        User user = userService.findUserByAccount(account);
        if (!userService.isPasswordCorrect(user, password)) {
            handleFailedLogin(response);
            return;
        }

        handleSuccessfulLogin(response, user);
    }

    private void handleSuccessfulLogin(HttpResponse response, User user) {
        HttpCookie httpCookie = new HttpCookie();
        httpCookie.addSessionId();

        response.setStatusLine(HttpStatus.FOUND);
        response.setHeader("Location", "/login.html");
        response.setHeader("Set-Cookie", httpCookie.toString());

        sessionService.registerSession(httpCookie.getSessionId(), user);
        System.out.println(user);
    }

    private void handleFailedLogin(HttpResponse response) throws IOException, URISyntaxException {
        String path = "static/401.html";
        String file = ResourceReader.readResource(path);

        response.setStatusLine(HttpStatus.UNAUTHORIZED);
        response.setContentType(ContentType.TEXT_HTML);
        response.setBody(file);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatusLine(HttpStatus.OK);

        HttpCookie cookie = request.getCookie();
        String path = determinePagePath(cookie);
        String resource = ResourceReader.readResource(path);
        response.setContentType(ContentType.TEXT_HTML);
        response.setBody(resource);
    }

    private String determinePagePath(HttpCookie cookie) {
        if (sessionService.isSessionExist(cookie.getSessionId())) {
            return "static/index.html";
        }
        return "static/login.html";
    }
}
