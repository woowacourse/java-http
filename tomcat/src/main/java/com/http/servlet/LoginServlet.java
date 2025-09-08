package com.http.servlet;

import com.http.enums.HttpStatus;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.HttpStatusException;
import com.techcourse.exception.UnAuthorizedException;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Map;
import org.apache.catalina.domain.Session;
import org.apache.catalina.domain.request.HttpRequest;
import org.apache.catalina.domain.response.HttpResponse;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.catalina.util.FileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet implements HttpServlet {

    private static final String LOGIN_FILE_NAME = "login.html";

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        // 이미 로그인된 상태인지 확인
        final Session session = request.getSession(false); // 세션이 없으면 null 반환
        final User user = getUser(session);
        if (session != null && user != null) {
            // 이미 로그인된 상태면 index.html로 리다이렉트
            response.setStatus(HttpStatus.FOUND);
            response.addHeader("Location", "/index.html");
            log.info("로그인된 사용자입니다. account={}", user.getAccount());
            return;
        }

        // 로그인되지 않은 상태면 로그인 페이지 표시
        final byte[] loginHtml = FileParser.loadStaticResourceByFileName(LOGIN_FILE_NAME);
        response.setBody(loginHtml);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        log.debug("request = {}", request);
        final Map<String, String> form = request.parseBody();

        String account = form.get("account");
        String password = form.get("password");
        processLogin(request, account, password, response);
    }

    private void processLogin(HttpRequest request, String account, String password, HttpResponse httpResponse) {
        if (account == null || password == null) {
            log.error("account or password is null");
            throw new HttpStatusException("account or password is null", HttpStatus.BAD_REQUEST);
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        if (!user.checkPassword(password)) {
            throw new UnAuthorizedException("잘못된 인증입니다.");
        }

        // 세션 생성 및 사용자 정보 저장
        Session session = request.getSession(true);
        session.setAttribute("user", user);
        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.addHeader("Location", "/index.html");

        log.info("로그인 성공 아이디={}, 세션ID={}", account, session.getId());
    }

    private User getUser(Session session) {
        if (session == null) {
            return null;
        }

        return (User) session.getAttribute("user");
    }
}
