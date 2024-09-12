package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        response.addFileBody("/register.html");
    }

    @Override
    protected void doPost(Request request, Response response) throws Exception {
        Map<String, String> userInfo = request.parseBody();
        String account = userInfo.get("account");
        String email = userInfo.get("email");
        String password = userInfo.get("password");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.sendRedirection("/index.html");
    }
}
