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

import java.util.Map;

public class SignupController extends AbstractController {

    private static final String MAIN_PAGE = "/dashboard.html";

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
        Map<String, String> params = parseFormBody(request);
        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        String sessionId = SessionUtil.createSession(user);
        return ResponseUtil.buildLoginSuccessResponse(sessionId, MAIN_PAGE, "HTTP/1.1");
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
