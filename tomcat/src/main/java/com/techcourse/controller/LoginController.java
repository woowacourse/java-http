package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.MimeType;
import org.apache.coyote.http.Session;
import org.apache.coyote.http.SessionManager;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.Path;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.coyote.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

import static org.apache.coyote.util.Constants.STATIC_RESOURCE_LOCATION;

public class LoginController extends AbstractController {

    private static final String REDIRECT_LOCATION = "/index.html";
    private static final String UNAUTHORIZED_LOCATION = "/401.html";

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        if (request.getCookie().hasCookieName("JSESSIONID")) {
            return alreadyLoggedIn(request);
        }
        try {
            Path path = request.getPath();
            return generateResponse(STATIC_RESOURCE_LOCATION + path.getUri() + MimeType.HTML.getExtension(), HttpStatus.OK);
        } catch (NullPointerException e) {
            return new NotFoundController().doGet(request);
        } catch (IOException e) {
            return new InternalServerErrorController().doGet(request);
        }
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        if (request.getCookie().hasCookieName("JSESSIONID")) {
            return alreadyLoggedIn(request);
        }

        Path path = request.getPath();

        HttpResponse response = generateResponse(STATIC_RESOURCE_LOCATION + path.getUri() + MimeType.HTML.getExtension(), HttpStatus.FOUND);
        HttpResponse loginFailResponse = generateResponse(STATIC_RESOURCE_LOCATION + UNAUTHORIZED_LOCATION, HttpStatus.UNAUTHORIZED);

        if (loginSuccess(request, response)) {
            return response;
        }
        return loginFailResponse;
    }

    private HttpResponse alreadyLoggedIn(HttpRequest request) throws Exception {
        Path path = request.getPath();

        HttpResponse response = generateResponse(STATIC_RESOURCE_LOCATION + path.getUri() + MimeType.HTML.getExtension(), HttpStatus.FOUND);
        response.setRedirectLocation(REDIRECT_LOCATION);

        return response;
    }

    private boolean loginSuccess(HttpRequest request, HttpResponse response) {
        Map<String, String> parsedBody = StringUtils.separateKeyValue(request.getBody());
        String account = parsedBody.get("account");
        String password = parsedBody.get("password");
        User user = InMemoryUserRepository.findByAccount(account).get();
        if (user.checkPassword(password)) {
            log.info(user.toString());
            response.setRedirectLocation(REDIRECT_LOCATION);
            HttpCookie cookie = getHttpCookie(user);
            response.setCookie(cookie.toCookieResponse());
            return true;
        }
        return false;
    }

    private HttpCookie getHttpCookie(User user) {
        Session session = Session.getSession();
        session.setUser(user);
        sessionManager.add(session);
        HttpCookie cookie = new HttpCookie();
        cookie.setSessionId(session.getId());
        return cookie;
    }
}
