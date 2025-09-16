package com.techcourse.presentation;

import com.techcourse.application.UserService;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String BASE_URL = "/register";

    private final UserService userService;

    public RegisterController(final UserService userService) {
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

        return renderStaticPage(BASE_URL + ".html", request);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final Map<String, String> params = request.params();
        final String account = params.get("account");
        final String password = params.get("password");
        final String email = params.get("email");

        if (isInvalidRegisterRequest(account, password, email)) {
            log.debug("요청 파라미터: {}", params);
            return createBadRequestResponse("적절하지 않은 회원가입 요청입니다.", request);
        }

        try {
            final User user = userService.register(account, password, email);
            if (user == null) {
                return renderStaticPage(BASE_URL + ".html", request);
            }

            return createSuccessResponseWithSession(user, request);
        } catch (IllegalArgumentException e) {
            return createBadRequestResponse(e.getMessage(), request);
        }
    }

    private boolean isInvalidRegisterRequest(String account, String password, String email) {
        return account == null || password == null || email == null ||
               account.isBlank() || password.isBlank() || email.isBlank();
    }

    private HttpResponse createBadRequestResponse(String errorMessage, HttpRequest request) {
        return HttpResponse.fromRequest(request)
                .badRequest()
                .setPlainTextContent(errorMessage)
                .build();
    }

    private HttpResponse createSuccessResponseWithSession(User user, HttpRequest request) {
        final Session session = request.getSession(true);
        session.setAttribute(user.getAccount(), user);
        SessionManager.getInstance().add(session);

        final String body = "회원가입이 완료되었습니다.";
        return HttpResponse.fromRequest(request)
                .seeOther()
                .location("/index.html")
                .setSession(session)
                .setPlainTextContent(body)
                .build();
    }

    @Override
    protected String getBasePath() {
        return BASE_URL;
    }
}
