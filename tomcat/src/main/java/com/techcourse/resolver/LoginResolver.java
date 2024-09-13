package com.techcourse.resolver;

import com.techcourse.session.Session;
import com.techcourse.session.SessionManager;
import com.techcourse.db.InMemoryUserRepository;
import java.util.Map;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;


@Location("/login")
public class LoginResolver extends HttpRequestResolver {

    private final SessionManager sessionManager = new SessionManager();

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if (sessionManager.findSession(request.getCookie()).isEmpty()) {
            System.out.println(request.getCookie());
            String body = new ResourceFinder(request.getLocation()).getStaticResource(response);
            response.setBody(body);
            return;
        }
        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Location", "/index.html");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> payload = request.getPayload();
        if (InMemoryUserRepository.findByAccount(payload.get("account")).isEmpty()) {
            throw new IllegalArgumentException("account already exists");
        }
        Session session = sessionManager.findSession(payload.get("account")).orElseThrow();
        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Location", "/index.html");
        response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId() + " ");
    }
}
