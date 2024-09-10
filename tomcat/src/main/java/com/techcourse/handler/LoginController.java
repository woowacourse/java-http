package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setContentType(ContentType.HTML);
        response.setHttpStatus(HttpStatus.OK);
        response.setResponseBody(readResource("login.html"));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        HttpBody body = request.getBody();
        Header header = response.getHeader();
        if (!isLoggedIn(body)) {
            response.sendRedirect("401.html");
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("user", getUser(body));
        header.appendJSessionId(session.getId());
        response.sendRedirect("index.html");
    }

    private boolean isLoggedIn(HttpBody body) {
        String password = body.get("password").orElse("");

        return body.get("account")
                .flatMap(InMemoryUserRepository::findByAccount)
                .map(it -> it.checkPassword(password))
                .orElse(false);
    }

    private User getUser(HttpBody body) {
        String account = body.get("account").orElseThrow();
        return InMemoryUserRepository.findByAccount(account).orElseThrow();
    }
}
