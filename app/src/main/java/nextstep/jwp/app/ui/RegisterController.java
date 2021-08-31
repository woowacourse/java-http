package nextstep.jwp.app.ui;

import java.io.IOException;
import nextstep.jwp.app.db.InMemoryUserRepository;
import nextstep.jwp.app.domain.User;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        return response.forward(request.getPath() + ".html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        HttpResponse response = new HttpResponse();
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return response.sendRedirect("/500.html", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        InMemoryUserRepository.save(new User(null, account, password, email));
        return response.sendRedirect("/index.html");
    }
}