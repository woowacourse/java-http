package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.model.User;

import java.util.Map;

public class LoginController extends AbstractController{
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> requestBody = request.getRequestBody();
        if (requestBody.size() > 0) {
            String account = request.getParameter("account");
            String password = request.getParameter("password");

            User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
            validateUserPassword(response, user, password);
            response.redirect("/401.html");
        }
        response.forward("/login.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.forward("/login.html");
    }

    private void validateUserPassword(HttpResponse response, User user, String password) {
        if (user.checkPassword(password)) {
            response.redirect("/index.html");
        }
    }
}
