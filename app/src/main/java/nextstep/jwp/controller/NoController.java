package nextstep.jwp.controller;

import java.util.Map;
import java.util.Objects;
import nextstep.jwp.HttpRequest;
import nextstep.jwp.HttpResponse;
import nextstep.jwp.HttpStatus;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class NoController implements Controller {

    @Override
    public HttpResponse get(HttpRequest request) {
        return null;
    }

    @Override
    public HttpResponse post(HttpRequest request) {
        return null;
    }
}
