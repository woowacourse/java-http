package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;

public class RegisterController extends AbstractController {

    private final StaticResourceController staticResources = new StaticResourceController();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        staticResources.sendFile("/register.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.parameter("account");
        String password = request.parameter("password");
        String email = request.parameter("email");
        if (account == null || password == null || email == null) {
            response.sendBadRequest();
            return;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        response.sendRedirect("/index.html");
    }
}
