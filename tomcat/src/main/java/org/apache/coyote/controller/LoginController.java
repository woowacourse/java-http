package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import static org.apache.coyote.http11.Status.FOUND;
import static org.apache.coyote.http11.Status.OK;

public class LoginController extends AbstractController {

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse();

        String requestBody = httpRequest.getRequestBody();

        if (httpRequest.getPath().startsWith("/register")) {
            Session session = register(requestBody);

            httpResponse.setStatusLine(FOUND);
            httpResponse.setCookie(session.getId());
            httpResponse.setLocation("/index.html");
        } else {
            Session session = authenticate(requestBody);

            if (session == null) {
                httpResponse.setStatusLine(FOUND);
                httpResponse.setLocation("/401.html");
            } else {
                httpResponse.setStatusLine(FOUND);
                httpResponse.setCookie(session.getId());
                httpResponse.setLocation("/index.html");
            }
        }

        return httpResponse;
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        HttpResponse httpResponse = new HttpResponse();

        if (httpRequest.getCookie() != null && getSession(httpRequest.getCookie()) != null) {
            Session session = getSession(httpRequest.getCookie());

            httpResponse.setStatusLine(FOUND);
            httpResponse.setCookie(session.getId());
            httpResponse.setLocation("/index.html");
        } else {
            String responseBody = getResource(httpRequest.getPath());

            httpResponse.setStatusLine(OK);
            httpResponse.setContentType(httpRequest.getContentType());
            httpResponse.setResponseBody(responseBody);
            httpResponse.setContentLength(responseBody.getBytes().length);
        }

        return httpResponse;
    }

    private Session authenticate(String requestBody) {
        String account = requestBody.split("&")[0].split("=")[1];
        String password = requestBody.split("&")[1].split("=")[1];

        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        if (optionalUser.isPresent() && optionalUser.get().checkPassword(password)) {
            String uuid = UUID.randomUUID().toString();
            Session session = new Session(uuid);
            session.setAttribute("user", session);
            SessionManager.add(session);

            return session;
        }

        return null;
    }

    private Session register(String requestBody) {
        String account = requestBody.split("&")[0].split("=")[1];
        String password = requestBody.split("&")[1].split("=")[1];
        String email = requestBody.split("&")[2].split("=")[1];

        InMemoryUserRepository.save(new User(account, password, email));
        return authenticate(requestBody);
    }

    private Session getSession(String cookie) {
        return SessionManager.findSession(cookie);
    }

    private String getResource(String path) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + path);
        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
