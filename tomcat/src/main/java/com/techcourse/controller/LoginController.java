package com.techcourse.controller;

import com.techcourse.StaticResourceReader;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.request.Method;
import org.apache.coyote.request.MyHttpRequest;
import org.apache.coyote.response.MyHttpResponse;
import org.apache.coyote.response.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Optional;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected List<Method> allowedMethods() {
        return List.of(Method.GET, Method.POST);
    }

    @Override
    protected void doPost(MyHttpRequest request, MyHttpResponse response) throws Exception {
        authenticate(request, response);
    }

    @Override
    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws Exception {
        Session session = request.getSession(false);
        if (session != null && getUser(session) != null) {
            response.sendRedirect("index.html");
            return;
        }

        response.setStatusCode(StatusCode.OK);
        response.setContentType(request.getContentType());
        final var responseBody = StaticResourceReader.read(request.getResourcePath())
                .orElseThrow(() -> new IllegalArgumentException("정적 리소스를 찾을 수 없습니다."));
        response.writeBody(responseBody);
    }

    private void authenticate(MyHttpRequest httpRequest, MyHttpResponse httpResponse) throws IOException {
        Map<String, String> params = httpRequest.getFormParameters();
        Optional<User> foundUser = findUserByAccount(params.get("account"));
        if (foundUser.isEmpty()) {
            log.info("authenticate failed: user not found");
            httpResponse.sendRedirect("401.html");
            return;
        }

        if (foundUser.get().checkPassword(params.get("password"))) {
            log.info("user matched={}", foundUser.get());
            final var session = httpRequest.getSession(true);
            session.setAttribute("user", foundUser.get());
            httpResponse.sendRedirect("index.html");
            return;
        }
        log.info("authenticate failed: incorrectly password");
        httpResponse.sendRedirect("401.html");
    }

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }

    private static Optional<User> findUserByAccount(String account) {
        return InMemoryUserRepository.findByAccount(account);
    }
}
