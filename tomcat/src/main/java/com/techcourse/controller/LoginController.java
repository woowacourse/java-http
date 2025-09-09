package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponseStatus;
import org.apache.coyote.http11.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Session session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(HttpResponseStatus.FOUND, "/index.html");
        }
        serveStaticFile("/login.html", response, "text/html;charset=utf-8");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getFormParam("account");
        String password = request.getFormParam("password");

        if (account == null || password == null) {
            response.sendRedirect(HttpResponseStatus.FOUND, "/401.html");
            return;
        }

        handleLogin(request, response, account, password);
    }

    private static void handleLogin(HttpRequest request, HttpResponse response, String account, String password)
            throws IOException {
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isPresent() && userOptional.get().checkPassword(password)) {
            log.info("user: {}", userOptional.get());
            Session session = request.getSession(true);
            session.setAttribute("user", userOptional.get());
            response.addCookie("JSESSIONID", session.getId());
            response.sendRedirect(HttpResponseStatus.FOUND, "/index.html");
            return;
        }
        response.sendRedirect(HttpResponseStatus.FOUND, "/401.html");
    }
}
