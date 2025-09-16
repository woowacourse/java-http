package com.techcourse.presentation;

import com.techcourse.application.UserService;
import com.techcourse.model.User;
import com.techcourse.util.ResourceWithType;
import com.techcourse.util.StaticResourceManager;
import java.nio.charset.StandardCharsets;
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

        final ResourceWithType resource = StaticResourceManager.getResource(BASE_URL + ".html");

        return HttpResponse.fromRequest(request)
                .contentType(resource.contentType())
                .setDefaultCharset()
                .contentLength(resource.content().getBytes(StandardCharsets.UTF_8).length)
                .body(resource.content())
                .build();
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final Map<String, String> params = request.params();
        final String account = params.get("account");
        final String password = params.get("password");

        if (account == null || password == null) {
            log.debug("요청 파라미터: {}", params);
            return createUnauthorizedResponse(request);
        }

        try {
            final User user = userService.login(account, password);
            if (user == null) {
                return createUnauthorizedResponse(request);
            }

            return createSuccessResponseWithSession(user, request);
        } catch (IllegalArgumentException e) {
            return createUnauthorizedResponse(request);
        }
    }

    private HttpResponse createUnauthorizedResponse(final HttpRequest request) {
        final ResourceWithType resource = StaticResourceManager.getResource("/401.html");

        return HttpResponse.fromRequest(request)
                .unauthorized()
                .contentType(resource.contentType())
                .setDefaultCharset()
                .contentLength(resource.content().getBytes(StandardCharsets.UTF_8).length)
                .body(resource.content())
                .build();
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
