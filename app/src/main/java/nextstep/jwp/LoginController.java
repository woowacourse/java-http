package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

import java.io.IOException;
import java.util.Optional;

public class LoginController extends AbstractController {

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
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.checkPassword(password)) {
                response.sendRedirect("/index.html");
            } else {
                response.sendRedirect("/401.html");
            }
        } else {
            response.sendRedirect("/401.html");
        }
    }
}
