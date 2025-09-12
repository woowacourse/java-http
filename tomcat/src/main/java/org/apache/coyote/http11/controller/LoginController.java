package org.apache.coyote.http11.controller;

import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request_response.HttpMethod;
import org.apache.coyote.http11.request_response.HttpStatus;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.request_response.request.HttpRequest;
import org.apache.coyote.http11.request_response.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public boolean supports(HttpRequest request) {
        return request.getRequestMethod().equals(HttpMethod.POST) && request.getUriPath().equals("/login");
    }

    @Override
    public HttpResponse service(HttpRequest request) {
        Session session = request.getSession(true);
        SessionManager.getInstance().changeSessionId(session);
        try {
            Map<String, String> formData = request.getFormData();
            User loginUser = login(formData);
            session.addAttribute("user", loginUser);
        } catch (UnauthorizedException e) {
            return HttpResponse.builder()
                .status(HttpStatus.Found)
                .header("Location", "/401.html")
                .body("")
                .build();
        }
        return HttpResponse.builder()
            .status(HttpStatus.Found)
            .header("Location", "/index.html")
            .body("")
            .cookie("JSESSIONID", session.getId())
            .build();
    }

    private User login(Map<String, String> queryParameters) {
        String account = queryParameters.get("account");
        String password = queryParameters.get("password");
        if (account == null || password == null) {
            throw new UnauthorizedException("account or password should be not null");
        }
        Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
        User user = findUser.orElseThrow(() -> new UnauthorizedException("Invalid account " + account));
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("Invalid password");
        }
        log.atInfo().log("user: {}", user);
        return user;
    }
}
