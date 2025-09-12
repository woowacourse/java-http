package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected Http11Response doPost(Http11Request http11Request) throws IOException {
        Map<String, String> parseQuery = http11Request.parseBody();

        String account = parseQuery.get("account");
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        if(!optionalUser.isEmpty() && optionalUser.get().checkPassword(parseQuery.get("password"))) {
            User user = optionalUser.get();
            log.info("user: {}", user);

            Session session = SessionManager.getInstance().createSession();
            session.setAttribute("user", user);

            Http11Cookie http11Cookie = new Http11Cookie("JSESSIONID", session.getId());
            List<Http11Cookie> cookies = new ArrayList<>();
            cookies.add(http11Cookie);

            String path = "index.html";
            String contentType = guessContentTypeByFileExtension(path);
            byte[] body = readFromResourcePath(path);

            return new Http11Response(body, contentType, Http11Status.FOUND, cookies);
        }

        String path = "401.html";
        String contentType = guessContentTypeByFileExtension(path);
        byte[] body = readFromResourcePath(path);

        return new Http11Response(body, contentType, Http11Status.FOUND);
    }

    @Override
    protected Http11Response doGet(Http11Request http11Request) throws IOException {
        Optional<String> optionalSessionId = http11Request.getSession("JSESSIONID");

        if(optionalSessionId.isPresent()) {
            String sessionId = optionalSessionId.get();

            if(SessionManager.getInstance().findSession(sessionId) != null) {
                Session session = SessionManager.getInstance().findSession(sessionId);
                log.info("JSESSIONID: {}", sessionId);

                User user = (User) session.getAttribute("user");
                log.info("user: {}", user);

                String path = "index.html";
                String contentType = guessContentTypeByFileExtension(path);
                byte[] body = readFromResourcePath(path);

                return new Http11Response(body,contentType, Http11Status.FOUND);
            }
        }

        String path = "login.html";
        String contentType = guessContentTypeByFileExtension(path);
        byte[] body = readFromResourcePath(path);

        return new Http11Response(body,contentType, Http11Status.FOUND);
    }
}
