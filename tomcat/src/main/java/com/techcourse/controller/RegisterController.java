package com.techcourse.controller;


import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.DuplicatedAccountException;
import com.techcourse.model.User;
import com.techcourse.session.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends HttpController {

    public RegisterController(String path) {
        super(path);
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        ResourceFinder.setStaticResponse(request, response);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getPayload().get("account");
        String password = request.getPayload().get("password");
        String email = request.getPayload().get("email");

        InMemoryUserRepository.findByAccount(account).ifPresent(x -> {
            throw new DuplicatedAccountException(account);
        });

        InMemoryUserRepository.save(new User(account, password, email));
    }
}
