package com.techcourse.servlet;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.AuthenticationException;
import com.techcourse.model.User;
import java.io.File;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.resource.ResourceParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String LOGIN_PAGE_PATH = "/login.html";

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    @Override
    public void service(HttpRequest req, HttpResponse resp) {
        if (req.isMethod(HttpMethod.GET)) {
            doGet(req, resp);
        } else if (req.isMethod(HttpMethod.POST)) {
            doPost(req, resp);
        } else {
            sendMethodNotAllowed(req, resp);
        }
    }

    @Override
    protected void doGet(HttpRequest req, HttpResponse resp) {
        Session session = req.getSession(true);
        User user = (User) session.getAttribute("user");
        if (user != null) {
            log.info("이미 로그인된 유저입니다. (account: {})", user.getAccount());
            resp.setRedirect(HttpStatus.FOUND, "/index.html");
            return;
        }

        resp.setResponse(HttpStatus.OK, LOGIN_PAGE_PATH);
    }

    @Override
    protected void doPost(HttpRequest req, HttpResponse resp) {
        if (!validateHasBody(req, ACCOUNT, PASSWORD)) {
            resp.setResponse(HttpStatus.BAD_REQUEST, LOGIN_PAGE_PATH);
            return;
        }

        try {
            User user = findUser(req);
            addUserSession(req, resp, user);
            resp.setRedirect(HttpStatus.FOUND, "/index.html");
        } catch (AuthenticationException e) {
            resp.setResponse(HttpStatus.UNAUTHORIZED, "/401.html");
        }
    }

    private User findUser(HttpRequest req) {
        String account = req.getBodyValue(ACCOUNT);
        String password = req.getBodyValue(PASSWORD);
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new AuthenticationException(
                        "찾으시는 유저 정보가 잘못되었습니다. (Account: %s, Password: %s)".formatted(account, password)));
    }

    private void addUserSession(HttpRequest req, HttpResponse resp, User user) {
        Session session = req.getSession(true);
        User userInSession = (User) session.getAttribute("user");

        if (userInSession != null) {
            log.info("이미 로그인된 유저입니다. (account: {})", user.getAccount());
            return;
        }
        session.addAttribute("user", user);
        resp.setCookie(new Cookie("JSESSIONID", session.getId()));
        log.info("로그인 성공 (Account: {})", user.getAccount());
    }

    @Override
    protected void doPut(HttpRequest req, HttpResponse resp) {
    }

    @Override
    protected void doDelete(HttpRequest req, HttpResponse resp) {
    }

    @Override
    protected void doPatch(HttpRequest req, HttpResponse resp) {
    }
}
