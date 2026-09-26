package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.setBody(readResource(), "text/html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        final String email = request.getParameter("email");
        if (isBlank(account) || isBlank(password) || isBlank(email)) {
            response.sendRedirect("/register");
            return;
        }
        InMemoryUserRepository.save(new User(account, password, email));
        response.sendRedirect("/index.html");
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }

    private byte[] readResource() throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream("static/register.html")) {
            if (resource == null) {
                throw new IOException("정적 리소스를 찾을 수 없습니다: /register.html");
            }
            return resource.readAllBytes();
        }
    }
}
