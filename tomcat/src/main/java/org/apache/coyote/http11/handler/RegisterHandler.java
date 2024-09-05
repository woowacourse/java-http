package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.Map;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterHandler extends AbstractRequestHandler {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.setResponseBodyFile(request);

        response.write();
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> queryString = request.getQueryString();
        if (
                queryString.containsKey("account") &&
                queryString.containsKey("password") &&
                queryString.containsKey("email")
        ) {
            try {
                register(queryString.get("account"), queryString.get("password"), queryString.get("email"));
                response.sendRedirect("/index.html");
            } catch (IllegalArgumentException e) {
                response.sendRedirect("/400.html");
            }
        } else {
            response.sendRedirect("/404.html");
        }
    }

    private void register(String account, String password, String email) {
        InMemoryUserRepository.findByAccount(account).ifPresent(user -> {
            throw new IllegalArgumentException("이미 존재하는 계정입니다.");
        });

        User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);
    }
}
