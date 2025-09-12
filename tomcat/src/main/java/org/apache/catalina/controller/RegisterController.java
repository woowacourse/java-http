package org.apache.catalina.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.coyote.handler.StaticResourceHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isSameMethod(("GET"))) {
            doGet(request, response);
            return;
        }
        if (request.isSameMethod(("POST"))) {
            doPost(request, response);
            return;
        }
        StaticResourceHandler.handleStaticResource(request, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        if (request.hasParameters()) {
            handleRegister(request, response);
            return;
        }
        StaticResourceHandler.handleStaticResource(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        StaticResourceHandler.handleStaticResource(request, response);
    }

    private void handleRegister(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        if (account == null || account.isBlank() || password == null || password.isBlank() || email == null
                || email.isBlank()) {
            response.redirect("/401.html");
            return;
        }

        Optional<User> findUser = InMemoryUserRepository.findByAccount(account);

        if (findUser.isPresent()) {
            if (findUser.get().checkPassword(password)) {
                log.info("register failure: account= {} already registered", account);
            } else {
                log.info("register failure: duplicate account= {}", account);
            }
            response.redirect("/register.html");
            return;
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("register success: account= {} email= {}", account, email);
        response.redirect("/index.html");
    }
}
