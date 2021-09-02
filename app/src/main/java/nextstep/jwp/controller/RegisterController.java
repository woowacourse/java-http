package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.model.User;

import java.util.Map;

public class RegisterController extends AbstractController {
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> requestBody = request.getRequestBody();
        if (requestBody.size() > 0) {
            String account = request.getParameter("account");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            InMemoryUserRepository.save(new User(2L, account, password, email));
            response.redirect("/index.html");
        }
        response.forward("/register.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.forward("/register.html");
    }
}
