package nextstep.jwp.ui.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.InternalServerException;
import nextstep.jwp.model.User;
import nextstep.jwp.ui.request.HttpRequest;
import nextstep.jwp.ui.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request,HttpResponse response) {
        response.forward(request.getPath() + ".html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new InternalServerException();
        }
        InMemoryUserRepository.save(new User(null, account, password, email));
        response.sendRedirect("/index.html");
    }
}
