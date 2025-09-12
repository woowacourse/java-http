package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;
import org.apache.coyote.util.UrlEncodedFormParser;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        response.sendResource("/register.html");
    }

    @Override
    protected void doPost(final Request request, final Response response) throws Exception {
        final Map<String, String> formData = UrlEncodedFormParser.parse(request.getMessageBody());
        final String account = formData.get("account");
        final String email = formData.get("email");
        final String password = formData.get("password");
        InMemoryUserRepository.save(new User(account, password, email));
        response.sendRedirection("/index.html");
    }
}
