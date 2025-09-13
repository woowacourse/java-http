package org.apache.catalina.controller;

import static org.apache.coyote.http11.Mime.HTML;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.catalina.storage.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.util.FileReader;
import org.apache.coyote.http11.util.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final var params = HttpRequestParser.parseQueryString(request.getBody());

        final String account = params.get("account");
        final String password = params.get("password");
        if (account == null || account.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException();
        }

        final var user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            response.setStatus(HttpStatus.UNAUTHORIZED);
            return ;
        }

        final var savedUser = user.get();
        if (savedUser.checkPassword(password)) {
            log.info("user : {}", savedUser);

            final var session = request.getSession(true);
            session.setAttribute("user", savedUser);

            final var sessionManager = SessionManager.getInstance();
            final var sessionCookie = sessionManager.generateSessionCookie(session);

            response.addCookie(sessionCookie);
            response.setRedirect("index.html");
            return ;
        }
        response.setStatus(HttpStatus.UNAUTHORIZED);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final var session = request.getSession(false);
        response.setContentType(HTML.getType());
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpStatus.OK);
            response.setBody(FileReader.readByName("login.html"));
            return ;
        }
        response.setRedirect("index.html");
    }
}
