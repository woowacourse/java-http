package org.apache.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.view.ViewUtils;

public class RegisterController implements Controller {

    @Override
    public boolean isProcessable(HttpRequest httpRequest) {
        return httpRequest.pathEquals("/register");
    }

    @Override
    public HttpResponse process(HttpRequest httpRequest) throws URISyntaxException, IOException {
        HttpResponse httpResponse = HttpResponse.createEmptyResponse(httpRequest);
        String method = httpRequest.getMethod();

        if (method.equals("GET")) {
            return doGet(httpResponse);
        }
        return doPost(httpRequest, httpResponse);
    }

    public HttpResponse doPost(HttpRequest httpRequest, HttpResponse httpResponse)
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

    public HttpResponse doGet(HttpResponse httpResponse)
            throws IOException, URISyntaxException {
        httpResponse.setHttpStatus(HttpStatus.OK);
        return ViewUtils.render(httpResponse, "/register.html");
    }
}
