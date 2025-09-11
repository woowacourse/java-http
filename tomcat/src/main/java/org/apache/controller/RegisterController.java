package org.apache.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public class RegisterController implements Controller {

    private final StaticController staticController = new StaticController();

    @Override
    public boolean isProcessable(HttpRequest httpRequest) {
        return httpRequest.pathEquals("/register");
    }

    @Override
    public HttpResponse process(HttpRequest httpRequest, HttpResponse httpResponse)
            throws URISyntaxException, IOException {
        String method = httpRequest.getMethod();

        if (method.equals("POST")) {
            httpResponse = postRegister(httpRequest, httpResponse);
        }

        if (method.equals("GET")) {
            httpResponse = getRegister(httpRequest, httpResponse);
        }
        return httpResponse;
    }

    public HttpResponse postRegister(HttpRequest httpRequest, HttpResponse httpResponse)
            throws IOException, URISyntaxException {
        try {
            String account = httpRequest.getBodyAttribute("account");
            String password = httpRequest.getBodyAttribute("password");
            String email = httpRequest.getBodyAttribute("email");

            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            httpResponse.redirect("/login.html");

            return httpResponse;
        } catch (Exception e) {
            // TODO: 회원가입 실패 시 예외처리
        }

        return httpResponse;
    }

    public HttpResponse getRegister(HttpRequest httpRequest, HttpResponse httpResponse)
            throws IOException, URISyntaxException {
        return staticController.process(httpRequest, httpResponse);
    }
}
