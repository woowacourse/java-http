package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.AbstractHandler;
import org.apache.coyote.http11.ForwardResult;
import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeaderKey;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatus;

import java.net.URI;

public class PostLoginHandler extends AbstractHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        URI uri = httpRequest.uri();
        String path = uri.getPath();

        return "/login".equals(path) && httpRequest.httpMethod().isPost();
    }

    @Override
    protected ForwardResult forward(HttpRequest httpRequest, Manager sessionManager) {
        HttpBody body = httpRequest.body();
        Header header = Header.empty();
        String redirectionPath = "401.html";

        if (isLoggedIn(body)) {
            HttpSession session = findSessionOrCreate(sessionManager, createCookie(httpRequest));
            session.setAttribute("user", getUser(body));
            header.append(HttpHeaderKey.SET_COOKIE, getSessionKey() + "=" + session.getId());
            redirectionPath = "index.html";
        }

        header.append(HttpHeaderKey.LOCATION, redirectionPath);
        return new ForwardResult(HttpStatus.FOUND, header);
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
