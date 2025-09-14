package com.techcourse.presentation;

import com.techcourse.application.LoginService;
import com.techcourse.model.User;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    private static final String BASE_URL = "/register";

    private final LoginService loginService;

    public RegisterController(final LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        final Session session = request.getSession(false);
        if (session != null) {
            return HttpResponse.builder()
                    .protocol(request.requestLine().getProtocol())
                    .found()
                    .location("http://localhost:8080/index.html")
                    .build();
        }

        return renderStaticPage(BASE_URL + ".html", request.requestLine().getProtocol());
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
            final User user = loginService.register(account, password, email);
            if (user == null) {
                return renderStaticPage(BASE_URL + ".html", request.requestLine().getProtocol());
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
        return HttpResponse.builder()
                .protocol(request.requestLine().getProtocol())
                .badRequest()
                .contentType("text/plain;charset=utf-8")
                .contentLength(errorMessage.getBytes(StandardCharsets.UTF_8).length)
                .body(errorMessage)
                .build();
    }

    private HttpResponse createSuccessResponseWithSession(User user, HttpRequest request) {
        final Session session = request.getSession(true);
        session.setAttribute(user.getAccount(), user);
        SessionManager.getInstance().add(session);

        final String body = "회원가입이 완료되었습니다.";
        return HttpResponse.builder()
                .protocol(request.requestLine().getProtocol())
                .seeOther()
                .location("http://localhost:8080/index.html")
                .addCookie("JSESSIONID" + "=" + session.getId())
                .contentType("text/html;charset=utf-8")
                .contentLength(body.getBytes(StandardCharsets.UTF_8).length)
                .body(body)
                .build();
    }

    @Override
    protected String getBasePath() {
        return BASE_URL;
    }
}
