package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.session.JSessionIdGenerator;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.Optional;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_PAGE_URI = "/login.html";
    private static final String UNAUTHORIZED_PAGE_URI = "/401.html";
    private static final String INDEX_PAGE_URI = "/index.html";

    private final SessionManager sessionManager = new SessionManager();

    @Override
    public ResponseEntity service(HttpRequest request) {
        HttpRequestStartLine httpRequestStartLine = request.getHttpRequestStartLine();
        HttpRequestBody httpRequestBody = request.getHttpRequestBody();

        HttpMethod httpMethod = httpRequestStartLine.getHttpMethod();
        String requestURI = httpRequestStartLine.getPath();
        String account = httpRequestBody.find("account");

        if (httpMethod == HttpMethod.GET && account == null) {
            return ResponseEntity.builder()
                    .httpStatus(HttpStatus.OK)
                    .contentType(generateContentType(requestURI))
                    .location(LOGIN_PAGE_URI)
                    .build();
        }

        String password = httpRequestBody.find("password");

        User findAccount = findAccount(account);
        boolean isCorrectPassword = findAccount.checkPassword(password);

        if (!isCorrectPassword) {
            return handleLoginFail(requestURI, findAccount);
        }

        return handleLoginSuccess(requestURI, findAccount);
    }

    private ContentType generateContentType(String requestURI) {
        if (requestURI.endsWith(".css")) {
            return ContentType.CSS;
        }
        return ContentType.HTML;
    }

    private ResponseEntity handleLoginFail(String requestURI, User findAccount) {
        log.info("account {} 비밀번호 불일치로 로그인 실패", findAccount.getAccount());
        return ResponseEntity
                .builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .contentType(generateContentType(requestURI))
                .location(UNAUTHORIZED_PAGE_URI)
                .build();
    }

    private ResponseEntity handleLoginSuccess(String requestURI, User findAccount) {
        log.info("account {} 로그인 성공", findAccount.getAccount());
        ResponseEntity responseEntity = ResponseEntity
                .builder()
                .httpStatus(HttpStatus.FOUND)
                .contentType(generateContentType(requestURI))
                .location(INDEX_PAGE_URI)
                .build();
        String jSessionId = JSessionIdGenerator.generateRandomSessionId();
        responseEntity.setCookie("JSESSIONID", jSessionId);
        Session session = new Session(jSessionId);
        sessionManager.add(session);
        return responseEntity;
    }

    private User findAccount(String account) {
        Optional<User> findAccount = InMemoryUserRepository.findByAccount(account);
        if (findAccount.isEmpty()) {
            log.info("해당하는 Account를 찾을 수 없음! account={}", account);
            throw new NoSuchElementException();
        }

        return findAccount.get();
    }
}
