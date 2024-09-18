package com.techcourse.resolver;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UnknownAccountException;
import com.techcourse.session.SessionManager;
import java.util.Map;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;


@Location("/login")
public class LoginResolver extends HttpRequestResolver {
    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if (request.containsHeader("Cookie") && SessionManager.isValidJSessionId(request.getCookie())) {
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
