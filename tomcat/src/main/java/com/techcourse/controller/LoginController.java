package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnknownAccountException;
import com.techcourse.session.SessionManager;
import java.util.Map;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends HttpController {
    public LoginController(String path) {
        super(path);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (request.containsHeader("Cookie")) {
            System.out.println(request.getCookie());
            response.setStatus(HttpStatus.FOUND);
            response.addHeader("Content-Type", "text/html");
            response.addHeader("Location", "/index.html");
            return;
        }
        String body = new ResourceFinder(request.getLocation(), request.getExtension()).getStaticResource(response);
        response.setBody(body);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> payload = request.getPayload();
        String account = payload.get("account");
        if (InMemoryUserRepository.findByAccount(account).isEmpty()) {
            throw new UnknownAccountException(account);
        }
        String jSession = SessionManager.findSession(account)
                .getAttribute("JSESSIONID");
        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Location", "/index.html");
        response.addHeader("Set-Cookie", "JSESSIONID=" + jSession + " ");
    }
}
