package com.techcourse.servlet;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.resource.ResourceParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    public void service(HttpRequest req, HttpResponse resp) {
        if (req.isMethod(HttpMethod.GET)) {
            doGet(req, resp);
        } else if (req.isMethod(HttpMethod.POST)) {
            doPost(req, resp);
        }
    }

    @Override
    protected void doGet(HttpRequest req, HttpResponse resp) {
        File requestFile = ResourceParser.getRequestFile("/register.html");
        resp.setResponse(HttpStatus.OK, requestFile);
    }

    @Override
    protected void doPost(HttpRequest req, HttpResponse resp) {
        if (!validateHasBody(req, ACCOUNT, PASSWORD, EMAIL)) {
            resp.setResponse(HttpStatus.BAD_REQUEST, ResourceParser.getRequestFile("/register.html"));
            return;
        }

        try {
            User user = registerUser(req);
            resp.setResponse(HttpStatus.FOUND, "/index.html");
            log.info("회원가입 성공 (account: {})", user.getAccount());
        } catch (IllegalArgumentException e) {
            log.warn("회원가입 실패", e);
            resp.setResponse(HttpStatus.BAD_REQUEST, "/register.html");
        }
    }

    private User registerUser(HttpRequest req) {
        String account = req.getBodyValue(ACCOUNT);
        String password = req.getBodyValue(PASSWORD);
        String email = req.getBodyValue(EMAIL);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return user;
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
