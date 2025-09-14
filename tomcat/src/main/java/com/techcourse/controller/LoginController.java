package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.SessionUtil;
import org.apache.catalina.controller.StaticFileResponseBuilder;
import org.apache.catalina.ResponseUtil;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String MAIN_PAGE = "/dashboard.html";
    private static final String LOGIN_FAILED_PAGE = "/401.html";

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        User currentUser = SessionUtil.getCurrentUser(request);
        if (currentUser != null) {
            return ResponseUtil.buildRedirectResponse(MAIN_PAGE, request.version());
        }
        String path = request.path();
        String filePath = path.startsWith("/") ? path.substring(1) : path;
        
        return StaticFileResponseBuilder.buildStaticFileResponse(filePath + ".html", request.version());
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        Map<String, String> formData = parseFormBody(request);
        String account = formData.get("account");
        String password = formData.get("password");

        HttpResponse loginResponse = tryAuthenticate(account, password);
        if (loginResponse != null) {
            return loginResponse;
        }

        return ResponseUtil.buildRedirectResponse(LOGIN_FAILED_PAGE, "HTTP/1.1");
    }

    private HttpResponse tryAuthenticate(String account, String password) {
        if (account != null && password != null) {
            User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
            if (user.checkPassword(password)) {
                String sessionId = SessionUtil.createSession(user);

                log.info("로그인 성공: {}", user);
                return ResponseUtil.buildLoginSuccessResponse(sessionId, MAIN_PAGE, "HTTP/1.1");
            }
        }
        return null;
    }

    public Map<String, String> parseFormBody(HttpRequest httpRequest) {
        String body = httpRequest.body();
        Map<String, String> formData = new HashMap<>();

        if (body == null || body.isEmpty()) {
            return formData;
        }

        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                formData.put(keyValue[0], keyValue[1]);
            }
        }

        return formData;
    }
}
