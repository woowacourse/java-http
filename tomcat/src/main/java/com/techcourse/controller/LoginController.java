package com.techcourse.controller;

import static org.apache.coyote.http11.utils.UriUtils.getParameters;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.loader.ResourceLoader;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.utils.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String DEFAULT_PATH = "/login.html";

    public LoginController(final ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpCookie httpCookie = request.getCookies();
        if (httpCookie.contains("JSESSIONID")) {
            final String sessionId = httpCookie.getValue("JSESSIONID");
            if (SessionManager.getInstance().findSession(sessionId).isPresent()) {
                response.redirect("/index.html");
                return;
            }
        }
        try {
            response.ok(resourceLoader.getResourceAsBytes(DEFAULT_PATH), UriUtils.getMimeType(DEFAULT_PATH));
        } catch (FileNotFoundException e) {
            handleError(response, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final Map<String, String> queryParams = getParameters(
                request.getQueryString(),
                request.getBodyAsString()
        );

        final String account = queryParams.get("account");
        final String password = queryParams.get("password");

        if (account == null || password == null || account.isBlank() || password.isBlank()) {
            handleError(response, HttpStatus.BAD_REQUEST);
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : {}", user.get());
            final Session session = Session.create();
            session.setAttribute("user", user.get());
            SessionManager.getInstance().add(session);
            request.addCookie("JSESSIONID", session.getId());
            response.redirect("/index.html", request.getCookies());
            return;
        }

        response.redirect("/401.html");
    }
}
