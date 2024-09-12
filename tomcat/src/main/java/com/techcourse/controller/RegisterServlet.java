package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.coyote.http11.Servlet;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.response.HttpServletResponse;

public class RegisterServlet implements Servlet {

    private static final String ACCOUNT_FORM_DATA = "account";
    private static final String PASSWORD_FORM_DATA = "password";
    private static final String EMAIL_FORM_DATA = "email";
    private static final String REGISTER_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final AtomicLong userIdGenerator = new AtomicLong(2L);


    @Override
    public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String account = request.getFormData(ACCOUNT_FORM_DATA);
        String password = request.getFormData(PASSWORD_FORM_DATA);
        String email = request.getFormData(EMAIL_FORM_DATA);

        User user = new User(userIdGenerator.getAndIncrement(), account, password, email);
        InMemoryUserRepository.save(user);
        response.redirect(REGISTER_SUCCESS_REDIRECT_URI);
    }
}
