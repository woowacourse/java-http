package nextstep.jwp.controller;

import java.util.Map;
import java.util.Objects;
import nextstep.jwp.HttpRequest;
import nextstep.jwp.HttpResponse;
import nextstep.jwp.db.InMemoryUserRepository;

public class LoginController implements Controller {

    @Override
    public void get(HttpRequest request, HttpResponse response) {
        Map<String, String> queryParams = request.extractURIQueryParams();
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        if (!Objects.isNull(account) && !Objects.isNull(password)) {
            InMemoryUserRepository.findByAccount(account)
                .ifPresent((user) -> {
                    if (user.checkPassword(password)) {
                        System.out.println(user);
                    }
                });
        }
    }

    @Override
    public void post(HttpRequest request, HttpResponse response) {

    }

    @Override
    public void put(HttpRequest request, HttpResponse response) {

    }

    @Override
    public void patch(HttpRequest request, HttpResponse response) {

    }

    @Override
    public void delete(HttpRequest request, HttpResponse response) {

    }
}
