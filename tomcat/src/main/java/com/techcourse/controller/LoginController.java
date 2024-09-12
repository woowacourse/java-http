package com.techcourse.controller;

import static org.apache.catalina.webresources.FileResource.NOT_FOUND_RESOURCE_URI;
import static org.apache.catalina.webresources.FileResource.UN_AUTHORIZED_RESOURCE_URI;

import java.util.Map;
import java.util.Objects;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.servlet.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.tomcat.util.http.ResourceURI;
import org.apache.tomcat.util.http.header.HttpCookie;
import org.apache.tomcat.util.http.header.HttpHeaderType;
import org.apache.tomcat.util.http.parser.QueryStringParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final ResourceURI LOGIN_RESOURCE_URI = new ResourceURI("/login.html");
    private static final ResourceURI REDIRECT_RESOURCE_URI = new ResourceURI("/index.html");

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Session session = request.getSession(false);
        if (Objects.nonNull(session)) {
            response.sendRedirect(REDIRECT_RESOURCE_URI);
            return;
        }
        response.writeStaticResource(LOGIN_RESOURCE_URI);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Session session = request.getSession(true);
        Map<String, String> parsed = QueryStringParser.parse(request.httpBody().body());
        String account = parsed.get("account");
        String password = parsed.get("password");
        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(user -> {
                            if (user.checkPassword(password)) {
                                login(user, session, response);
                                return;
                            }
                            loginWithInvalidPassword(user, password, response);
                        },
                        () -> loginWithInvalidAccount(account, response));
    }

    private void login(User user, Session session, HttpResponse response) {
        log.error("user: {}, 로그인에 성공하였습니다.", user);
        if (session.isNew()) {
            session.setAttributes("userAccount", user.getAccount());
            new SessionManager().add(session);
            response.addHeader(HttpHeaderType.SET_COOKIE,
                    HttpCookie.SESSION_ID_IDENTIFICATION + HttpCookie.DELIMITER + session.getSessionId());
        }
        response.sendRedirect(REDIRECT_RESOURCE_URI);
    }

    private void loginWithInvalidPassword(User user, String password, HttpResponse response) {
        log.error("user: {}, inputPassword={}, 비밀번호가 올바르지 않습니다.", user, password);
        response.sendRedirect(UN_AUTHORIZED_RESOURCE_URI);
    }

    private static void loginWithInvalidAccount(String account, HttpResponse response) {
        log.error("inputAccount={}, 해당하는 사용자를 찾을 수 없습니다.", account);
        response.sendRedirect(NOT_FOUND_RESOURCE_URI);
    }
}
