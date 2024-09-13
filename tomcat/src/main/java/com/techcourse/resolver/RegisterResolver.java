package com.techcourse.resolver;


import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.session.Session;
import com.techcourse.session.SessionManager;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

@Location("/register")
public class RegisterResolver extends HttpRequestResolver {

    private final SessionManager sessionManager = new SessionManager();

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String body = new ResourceFinder(request.getLocation()).getStaticResource(response);
        response.setBody(body);
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> payload = request.getPayload();
        if (InMemoryUserRepository.findByAccount(payload.get("account")).isPresent()) {
            throw new IllegalArgumentException("duplicated user name");
        }

        Session newSession = new Session(UUID.randomUUID().toString());
        newSession.setAttribute("JESSIONID", UUID.randomUUID().toString());
        sessionManager.add(newSession);

        InMemoryUserRepository.save(
                new User(payload.get("account"), payload.get("password"), payload.get("email"))
        );

        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
        response.addHeader("Set-Cookie", "JSESSIONID=" + newSession.getId());
    }
}
