package nextstep.jwp.service;

import static nextstep.jwp.model.UserInfo.ACCOUNT;
import static nextstep.jwp.model.UserInfo.EMAIL;
import static nextstep.jwp.model.UserInfo.PASSWORD;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.controller.DuplicateRegisterException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;

public class RegisterService implements Service {

    @Override
    public void doService(HttpRequest request, HttpResponse response) {
        Map<String, String> query = request.getQuery();
        Optional<User> findUser = InMemoryUserRepository
            .findByAccount(query.get(ACCOUNT.getInfo()));

        if (findUser.isPresent()) {
            throw new DuplicateRegisterException();
        }
        User user = new User(
            query.get(ACCOUNT.getInfo()),
            query.get(PASSWORD.getInfo()),
            query.get(EMAIL.getInfo())
        );
        InMemoryUserRepository.save(user);
    }
}
