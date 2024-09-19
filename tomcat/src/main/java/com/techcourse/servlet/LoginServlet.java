package com.techcourse.servlet;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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

        File requestFile = ResourceParser.getRequestFile("/login.html");
        resp.setResponse(HttpStatus.OK, requestFile);
    }

    @Override
    protected void doPost(HttpRequest req, HttpResponse resp) {
        if(!validateHasBody(req, ACCOUNT, PASSWORD)) {
            resp.setResponse(HttpStatus.BAD_REQUEST, ResourceParser.getRequestFile("/login.html"));
            return;
        }

        String account = req.getBodyValue(ACCOUNT);
        String password = req.getBodyValue(PASSWORD);

        Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
        if (findUser.isEmpty()) {
            log.error("찾으시는 유저가 존재하지 않습니다. (Account: {})", account);
            resp.setResponse(HttpStatus.UNAUTHORIZED, ResourceParser.getRequestFile("/401.html"));
            return;
        }
        if (!findUser.get().checkPassword(password)) {
            log.error("찾으시는 유저 정보가 잘못되었습니다. (Account: {}, Password: {})", account, password);
            resp.setResponse(HttpStatus.UNAUTHORIZED, ResourceParser.getRequestFile("/401.html"));
            return;
        }

        Session session = req.getSession(true);
        User user = (User) session.getAttribute("user");
        if (user != null) {
            log.info("이미 로그인된 유저입니다. (account: {})", user.getAccount());
        } else {
            session.addAttribute("user", findUser.get());
            resp.setCookie(new Cookie("JSESSIONID", session.getId()));
            log.info("로그인 성공 (Account: {})", findUser.get().getAccount());
        }
        resp.setRedirect(HttpStatus.FOUND, "/index.html");
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
