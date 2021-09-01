package nextstep.jwp.service;

import static nextstep.jwp.http.response.HttpStatus.FOUND;
import static nextstep.jwp.http.response.HttpStatus.UNAUTHORIZED;
import static nextstep.jwp.model.UserInfo.ACCOUNT;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.view.View;

public class LoginService implements Service {

    @Override
    public View doService(HttpRequest request, HttpResponse response) {
        Map<String, String> query = request.getQuery();
        Optional<User> user = InMemoryUserRepository
            .findByAccount(query.get(ACCOUNT.getInfo()));

        if (user.isPresent()) {
            response.forward(FOUND, request.getUri());
            return new View("/index");
        }
        response.forward(UNAUTHORIZED, request.getUri());
        return new View("/401");
    }
}
