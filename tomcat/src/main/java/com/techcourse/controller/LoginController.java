package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UserException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import org.apache.coyote.http11.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final LoginController instance = new LoginController();

    private LoginController() {
    }

    public void login(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        FileReader fileReader = FileReader.getInstance();
        request.setHttpRequestPath("/login");
        String account = request.getRequestBodyValue("account");
        String password = request.getRequestBodyValue("password");
        try {
            User foundUser = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new UserException(account + "는 존재하지 않는 계정입니다."));
            if (foundUser.checkPassword(password)) {
                log.info("user : " + foundUser);
                response.setHttpStatusCode(HttpStatusCode.FOUND);
                request.setHttpRequestPath("/index.html");
                setSessionAtResponseHeader(foundUser, response);
            }
        } catch (UserException e) {
            request.setHttpRequestPath("/401.html");
            response.setHttpStatusCode(HttpStatusCode.UNAUTHORIZED);
        }

        response.setHttpResponseBody(fileReader.readFile(request.getHttpRequestPath()));
        response.setHttpResponseHeader("Content-Type", request.getContentType() + ";charset=utf-8");
        response.setHttpResponseHeader("Content-Length", String.valueOf(response.getHttpResponseBody().body().getBytes().length));
    }

    private void setSessionAtResponseHeader(User user, HttpResponse response) {
        String jsessionid = UUID.randomUUID().toString();
        Session session = new Session(jsessionid);
        session.setAttribute("user", user);
        SessionManager.add(session.getId(), session);
        response.setHttpResponseHeader("Set-Cookie", "JSESSIONID=" + jsessionid);
    }

    public static LoginController getInstance() {
        return instance;
    }
}
