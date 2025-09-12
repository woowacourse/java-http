package com.techcourse.controller;


import static com.techcourse.HttpStaus.*;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.processor.Http11Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.ResponseHandler;
import org.apache.coyote.session.HttpSession;
import org.apache.coyote.session.HttpSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpSessionManager SESSION_MANAGER = HttpSessionManager.getInstance();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getParameter("account").orElse(null);
        String password = request.getParameter("password").orElse(null);
        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (account == null || password == null) {
            ResponseHandler.sendStaticFile(response, "/400.html", BAD_REQUEST);
            return;
        }

        if (user.isEmpty() || !user.get().checkPassword(password)) {
            ResponseHandler.sendStaticFile(response,"/401.html", UNAUTHORIZED);
            return;
        }

        log.info(user.toString());

        Optional<HttpCookie> httpCookie = request.getCookie("JSESSIONID");
        String jsessionid = httpCookie.map(HttpCookie::getValue).orElseGet(() -> UUID.randomUUID().toString());
        HttpSession session = new HttpSession(jsessionid);
        session.setAttribute("user", user);
        SESSION_MANAGER.add(session);
        response.setCookie(new HttpCookie("JSESSIONID", jsessionid));

        ResponseHandler.redirect(response,"/index.html", FOUND);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Optional<String> sessionId = request.getCookieValue("JSESSIONID");

        if (sessionId.isPresent() && SESSION_MANAGER.containsKey(sessionId.get())) {
            ResponseHandler.redirect(response, "/index.html", FOUND);
        } else {
            ResponseHandler.sendStaticFile(response, "/login.html", OK);
        }
    }
}
