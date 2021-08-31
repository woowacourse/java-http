package nextstep.jwp.ui.controller;

import nextstep.jwp.ui.request.HttpRequest;
import nextstep.jwp.ui.response.HttpResponse;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

import java.io.IOException;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        return response.forward(request.getPath() + ".html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        HttpResponse response = new HttpResponse();
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return response.sendRedirect("/500.html");
        }
        InMemoryUserRepository.save(new User(null, account, password, email));
        return response.sendRedirect("/index.html");
    }
}
