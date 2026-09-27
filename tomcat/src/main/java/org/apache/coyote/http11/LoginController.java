package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.Session;

public class LoginController extends AbstractController {

    private final Session session;
    private final StaticResourceController staticResources = new StaticResourceController();

    public LoginController(Session session) {
        this.session = session;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (session.getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }
        staticResources.sendFile("/login.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.parameter("account");
        String password = request.parameter("password");
        if (account == null || password == null) {
            response.sendRedirect("/401.html");
            return;
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            session.setAttribute("user", user.get());
            response.sendRedirect("/index.html");
            return;
        }
        response.sendRedirect("/401.html");
    }
}
