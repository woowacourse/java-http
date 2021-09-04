package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;

import static nextstep.jwp.PageUrl.INDEX_PAGE;
import static nextstep.jwp.PageUrl.REGISTER_PAGE;

public class RegisterController extends AbstractController {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.forward(REGISTER_PAGE.getPath());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getRequestBodyParam("account");
        String password = request.getRequestBodyParam("password");
        String email = request.getRequestBodyParam("email");

        User user = new User(2, account, password, email);
        InMemoryUserRepository.save(user);

        response.redirect(INDEX_PAGE.getPath());
    }
}
