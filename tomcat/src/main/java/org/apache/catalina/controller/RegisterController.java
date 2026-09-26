package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected String doPost(HttpRequest httpRequest) throws Exception {
        String account = httpRequest.getRequestParam("account");
        String password = httpRequest.getRequestParam("password");
        String email = httpRequest.getRequestParam("email");
        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            return doGet(httpRequest);
        }
        User user = new User(account, password, email);
        return register(httpRequest, user);
    }

    private String register(HttpRequest httpRequest, User user) throws Exception {
        User exists = InMemoryUserRepository.findByAccount(user.getAccount()).orElse(null);
        if (exists != null) {
            return doGet(httpRequest);
        }
        InMemoryUserRepository.save(user);
        HttpResponse response = HttpResponse.of(httpRequest);
        return response.found("/index.html");
    }

    @Override
    protected String doGet(HttpRequest httpRequest) throws Exception {
        HttpResponse httpResponse = HttpResponse.of(httpRequest);
        return httpResponse.ok();
    }
}
