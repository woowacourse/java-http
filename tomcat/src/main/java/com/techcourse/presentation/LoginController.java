package com.techcourse.presentation;

import com.techcourse.application.UserService;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String BASE_URL = "/login";

    private final UserService userService;

    public LoginController(final UserService userService) {
        this.userService = userService;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        final Session session = request.getSession(false);
        if (session != null) {
            return HttpResponse.fromRequest(request)
                    .found()
                    .location("/index.html")
                    .build();
        }

        return renderStaticPage("/login.html", request);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final Map<String, String> params = request.params();
        final String account = params.get("account");
        final String password = params.get("password");

        if (account == null || password == null) {
            log.debug("요청 파라미터: {}", params);
            return renderStaticPage("/401.html", request);
        }

        try {
            final User user = userService.login(account, password);
            if (user == null) {
                return renderStaticPage("/401.html", request);
            }

            return createSuccessResponseWithSession(user, request);
        } catch (IllegalArgumentException e) {
            return renderStaticPage("/401.html", request);
        }
    }

    private HttpResponse createSuccessResponseWithSession(final User user, final HttpRequest request) {
        final Session session = request.getSession(true);
        session.setAttribute(user.getAccount(), user);
        SessionManager.getInstance().add(session);

        return HttpResponse.fromRequest(request)
                .found()
                .location("/index.html")
                .setSession(session)
                .build();
    }

    @Override
    protected String getBasePath() {
        return BASE_URL;
    }
}
