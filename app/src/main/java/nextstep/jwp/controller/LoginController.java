package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.HttpRequest;
import nextstep.jwp.model.HttpResponse;
import nextstep.jwp.model.HttpStatus;
import nextstep.jwp.model.User;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class LoginController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        Map<String, String> queryMap = request.getQueryMap();
        if (Objects.isNull(queryMap)) {
            return response.respond(request.getUri() + ".html");
        }
        String account = queryMap.get("account");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            return response.redirect("/index.html", HttpStatus.FOUND);
        }
        if (user.isEmpty()) {
            return response.redirect("404.html", HttpStatus.NOT_FOUND);
        }
        return response.respond(request.getUri() + ".html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        return null;
    }
}
