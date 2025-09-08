package org.apache.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.http11.Http11Processor;
import org.apache.http.HttpMethod;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public boolean isProcessableRequest(HttpRequest request) {
        return request.getMethod() == HttpMethod.GET
                && request.getUri().equals("/login")
                && request.checkQueryStringExistence("account")
                && request.checkQueryStringExistence("password");
    }

    @Override
    public void processRequest(HttpRequest request, HttpResponse response) {
        String account = request.getQueryString("account");
        String password = request.getQueryString("password");

        boolean isAuthenticated = authenticateUser(account, password);
        if (!isAuthenticated) {
            response.setStatusCode(StatusCode.FOUND);
            response.setHeader("Location", "/401.html");
            return;
        }

        response.setStatusCode(StatusCode.FOUND);
        response.setHeader("Location", "/index.html");
    }

    private boolean authenticateUser(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        user.ifPresent(value -> log.info("user : {}", value.toString()));
        return user.isPresent() && user.get().checkPassword(password);
    }
}
