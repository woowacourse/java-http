package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

import java.io.IOException;

public class RegisterController extends AbstractController {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        String method = request.getMethod();
        if ("GET".equals(method)) {
            doGet(request, response);
        }
        if ("POST".equals(method)) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(request.getPath() + ".html");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            response.sendRedirect("/500.html");
            return;
        }
        InMemoryUserRepository.save(new User(2, account, password, email));
        response.sendRedirect("/index.html");
    }
}
