package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.AbstractHandler;
import org.apache.coyote.http11.ForwardResult;
import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.HttpHeaderKey;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.QueryParameter;

import java.net.URI;
import java.util.Collections;

public class PostLoginHandler extends AbstractHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        URI uri = httpRequest.getUri();
        String path = uri.getPath();

        return "/login".equals(path) && httpRequest.getMethod().isPost();
    }

    @Override
    protected ForwardResult forward(HttpRequest httpRequest, Manager sessionManager) {
        QueryParameter queryParameter = httpRequest.body();
        Header header = new Header(Collections.emptyList());
        String redirectionPath = "401.html";

        if (isLoggedIn(queryParameter)) {
            HttpSession session = findSessionOrCreate(sessionManager, createCookie(httpRequest));
            session.setAttribute("user", getUser(queryParameter));
            header.append(HttpHeaderKey.SET_COOKIE, getSessionKey() + "=" + session.getId());
            redirectionPath = "index.html";
        }

        header.append(HttpHeaderKey.LOCATION, redirectionPath);
        return new ForwardResult(HttpStatus.FOUND, header);
    }

    private boolean isLoggedIn(QueryParameter queryParameter) {
        String password = queryParameter.get("password").orElse("");

        return queryParameter.get("account")
                .flatMap(InMemoryUserRepository::findByAccount)
                .map(it -> it.checkPassword(password))
                .orElse(false);
    }

    private User getUser(QueryParameter queryParameter) {
        String account = queryParameter.get("account").orElseThrow();
        return InMemoryUserRepository.findByAccount(account).orElseThrow();
    }
}
