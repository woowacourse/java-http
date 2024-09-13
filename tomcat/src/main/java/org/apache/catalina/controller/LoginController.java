package org.apache.catalina.controller;

import static org.reflections.Reflections.log;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.Headers;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController extends AbstractController {

    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String SUCCESS_LOCATION = "/index.html";
    public static final String FAIL_LOCATION = "/401.html";

    // 로그인 요청
    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final RequestBody body = request.getBody();
        final User user = InMemoryUserRepository.findByAccount(body.getByName(ACCOUNT))
                .orElseThrow(() -> new IllegalArgumentException("해당 account가 존재하지 않습니다."));
        if (user.checkPassword(body.getByName(PASSWORD))) {
            log.info("user : {}", user);
            // 세션ID 생성 책임은 누구에게
            final Session session = new Session();
            session.setAttribute("user", user);
            sessionManager.add(session);
            response.addCookie();
            response.sendRedirect(SUCCESS_LOCATION);
            return;
        }
        response.sendRedirect(FAIL_LOCATION);
    }

    // 로그인 페이지 이동
    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        // 이미 로그인 되어있는 상태면 바로 리다이렉트
        final Headers headers = request.getHeaders();
        headers.containsField("Cpppp dsmkox")
        if () {
        }

        // 아니면 로그인 페이지

    }
}
