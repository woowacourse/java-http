package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        response.sendResource("/register.html");
    }

    @Override
    protected void doPost(Request request, Response response) throws Exception {
        final String[] split = request.getMessageBody().split("&");
        final String account = split[0].split("=")[1];
        final String email = split[1].split("=")[1].replace("%40", "@");
        final String password = split[2].split("=")[1];
        InMemoryUserRepository.save(new User(account, password, email));
        response.sendRedirection("/index.html");
    }
}
