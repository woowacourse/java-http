package nextstep.jwp.app.ui;

import java.io.IOException;
import nextstep.jwp.app.db.InMemoryUserRepository;
import nextstep.jwp.app.domain.User;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.mvc.controller.AbstractController;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        return response.forward(request.getPath() + ".html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        String account = request.getBodyParameter("account");
        String password = request.getBodyParameter("password");
        String email = request.getBodyParameter("email");
        HttpResponse response = new HttpResponse();
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return response.sendRedirect(ERROR_500_HTML);
        }
        InMemoryUserRepository.save(new User(null, account, password, email));
        return response.sendRedirect(INDEX_HTML);
    }
}