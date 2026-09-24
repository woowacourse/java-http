package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {
    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getParameters().get("account");
        String password = request.getParameters().get("password");
        String email = request.getParameters().get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.sendRedirect("/index.html");
    }
}
