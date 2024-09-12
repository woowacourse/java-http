package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.SessionStorage;
import org.apache.coyote.http11.request.CookieManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends Controller {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String sessionId = CookieManager.getCookieValue(request.getHeaderValue("Cookie"), "JSESSIONID");
        if (sessionId == null) {
            response.setBodyWithStaticResource("/login.html");
        }
        User user = SessionStorage.get(sessionId);
        if (user == null) {
            response.setBodyWithStaticResource("/login.html");
        }

        log.info(user.toString());
        response.setStatusCode("302 Found");
        response.addHeader("Location", "/index.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String account = request.parseBodyParameter("account");
        String password = request.parseBodyParameter("password");

        if (account == null || password == null) {
            response.setStatusCode("401 Unauthorized");
            response.setBodyWithStaticResource("/401.html");
            return;
        }

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        if (optionalUser.isEmpty()) {
            response.setStatusCode("401 Unauthorized");
            response.setBodyWithStaticResource("/401.html");
            return;
        }

        User user = optionalUser.get();
        if (user.checkPassword(password)) {
            log.info(user.toString());
            String sessionId = SessionStorage.put(user);

            response.setStatusCode("302 Found");
            response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId);
            response.addHeader("Location", "/index.html");
            return;
        }
        response.setStatusCode("401 Unauthorized");
        response.setBodyWithStaticResource("/401.html");
    }
}
