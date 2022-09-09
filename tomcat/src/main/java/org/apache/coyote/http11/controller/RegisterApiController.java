package org.apache.coyote.http11.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.RequestBody;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.session.Cookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class RegisterApiController extends AbstractController {

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        RequestBody requestBody = httpRequest.getRequestBody();

        try {
            Map<String, Object> parameters = requestBody.getParameters();
            String account = (String) parameters.get("account");
            String password = (String) parameters.get("password");
            String email = (String) parameters.get("email");
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            setSession(httpRequest, user);
        } catch (Exception e) {
            httpResponse.sendError();
            return;
        }

        httpResponse.found("/index.html ")
                .addHeader("Set-Cookie", new Cookie(Map.of("JSESSIONID", httpRequest.getSession().getId())))
                .addHeader("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 ")
                .addHeader("Content-Length", "0 ");
    }

    private void setSession(HttpRequest httpRequest, User user) {
        SessionManager sessionManager = new SessionManager();
        Session session = httpRequest.getSession();
        session.setAttribute("user", user);
        sessionManager.add(session);
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String responseBody = getBody();

        httpResponse.ok(responseBody)
                .addHeader("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 ");
    }

    private String getBody() throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        File file = new File(resource.getFile());
        Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }
}
