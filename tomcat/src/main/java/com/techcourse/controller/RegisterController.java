package com.techcourse.controller;


import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.DuplicatedAccountException;
import com.techcourse.model.User;
import com.techcourse.session.Session;
import com.techcourse.session.SessionManager;
import java.util.Map;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends HttpController {

    public RegisterController(String path) {
        super(path);
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String body = new ResourceFinder(request.getLocation(), request.getExtension()).getStaticResource(response);
        response.setBody(body);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> payload = request.getPayload();
        String account = payload.get("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new DuplicatedAccountException(account);
        }

        Session newSession = new Session(account);
        SessionManager.add(newSession);

        InMemoryUserRepository.save(
                new User(account, payload.get("password"), payload.get("email"))
        );

        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
        response.addHeader("Set-Cookie", "JSESSIONID=" + newSession.getAttribute("JSESSIONID"));
    }
}
