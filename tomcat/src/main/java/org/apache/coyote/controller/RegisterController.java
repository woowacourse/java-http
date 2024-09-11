package org.apache.coyote.controller;

import static org.apache.coyote.http.StatusCode.BAD_REQUEST;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT_FIELD = "account";
    private static final String PASSWORD_FIELD = "password";
    private static final String EMAIL_FIELD = "email";

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getParameter(ACCOUNT_FIELD);
        String password = request.getParameter(PASSWORD_FIELD);
        String email = request.getParameter(EMAIL_FIELD);

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            response.setStatus(BAD_REQUEST);
        }
        InMemoryUserRepository.save(new User(account, password, email));

        response.setBody(readStaticResource("/login.html"));
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setBody(readStaticResource("/register.html"));
    }
}
