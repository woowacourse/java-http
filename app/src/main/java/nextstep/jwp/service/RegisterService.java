package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.handler.HttpBody;
import nextstep.jwp.handler.request.HttpRequest;
import nextstep.jwp.model.User;

public class RegisterService {

    public void register(HttpRequest request) {
        HttpBody httpBody = request.getBody();
        String account = httpBody.getBodyParams("account");
        String email = httpBody.getBodyParams("email");
        String password = httpBody.getBodyParams("password");
        User user = new User(InMemoryUserRepository.assignId(), account, password, email);
        InMemoryUserRepository.save(user);
    }
}
