package com.techcourse.servlet;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.catalina.servlet.HttpServlet;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class RegisterServlet extends HttpServlet {

    private static final String ACCOUNT_FORM_DATA = "account";
    private static final String PASSWORD_FORM_DATA = "password";
    private static final String EMAIL_FORM_DATA = "email";
    private static final String REGISTER_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final String PAGE_RESOURCE_PATH = "static/register.html";
    private static final AtomicLong userIdGenerator = new AtomicLong(2L);


    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.ok(PAGE_RESOURCE_PATH);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getFormData(ACCOUNT_FORM_DATA);
        String password = request.getFormData(PASSWORD_FORM_DATA);
        String email = request.getFormData(EMAIL_FORM_DATA);

        User user = new User(userIdGenerator.getAndIncrement(), account, password, email);
        InMemoryUserRepository.save(user);
        response.sendRedirect(REGISTER_SUCCESS_REDIRECT_URI);
    }
}
