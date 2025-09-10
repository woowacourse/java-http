package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.ResourceLoader;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class LoginController extends AbstractController {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        super.service(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        try {
            HttpCookie cookie = request.getCookies();
            String sessionId = cookie.getCookie("JSESSIONID");

            Session session = SessionManager.findSession(sessionId);

            if (session != null) {
                response.setStatusCode(HttpStatusCode.FOUND);
                response.sendRedirect("/index.html");
                return;
            }

            String path = request.getUri().getPath();
            response.setStatusCode(HttpStatusCode.OK);
            response.write(ResourceLoader.findResource(path));
            response.setMimeType(ResourceLoader.getMimeType(path));
            response.send();
        } catch (Exception e) {
            response.setStatusCode(HttpStatusCode.FOUND);
            response.sendRedirect("/401.html");
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        try {
            Map<String, String> formData = request.getBody().getFormData();
            String account = formData.get("account");
            String password = formData.get("password");

            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(IllegalArgumentException::new);

            boolean checkPassword = user.checkPassword(password);

            if (!checkPassword) {
                throw new IllegalArgumentException();
            }

            UUID uuid = UUID.randomUUID();
            Session session = new Session(uuid.toString());
            session.setAttribute("user", user);
            SessionManager.add(session);

            response.addCookie("JSESSIONID", uuid.toString());
            response.setStatusCode(HttpStatusCode.FOUND);
            response.sendRedirect("/index.html");
        } catch (IllegalArgumentException e) {
            response.setStatusCode(HttpStatusCode.FOUND);
            response.sendRedirect("/401.html");
        }
    }
}
