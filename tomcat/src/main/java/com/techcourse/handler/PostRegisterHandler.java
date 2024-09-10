package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.AbstractHandler;
import org.apache.coyote.http11.ForwardResult;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatus;

import java.net.URI;

public class PostRegisterHandler extends AbstractHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        URI uri = httpRequest.uri();
        String path = uri.getPath();

        return "/register".equals(path) && httpRequest.httpMethod().isPost();
    }

    @Override
    protected ForwardResult forward(HttpRequest httpRequest, Manager sessionManager) {
        registerNewUser(httpRequest);

        return new ForwardResult("index.html", HttpStatus.OK);
    }

    private void registerNewUser(HttpRequest httpRequest) {
        HttpBody body = httpRequest.body();
        String account = body.get("account").orElseThrow();
        String password = body.get("password").orElseThrow();
        String email = body.get("email").orElseThrow();

        InMemoryUserRepository.save(new User(account, password, email));
    }
}
