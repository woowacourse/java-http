package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.AuthManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.apache.coyote.http11.Status.FOUND;
import static org.apache.coyote.http11.Status.OK;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse();

        Session session = register(httpRequest);

        httpResponse.setStatusLine(FOUND);
        httpResponse.setCookie("JSESSIONID", session.getId());
        httpResponse.setLocation("/index.html");

        return httpResponse;
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        HttpResponse httpResponse = new HttpResponse();

        String responseBody = getResource(httpRequest.getPath() + ".html");

        httpResponse.setStatusLine(OK);
        httpResponse.setContentType(httpRequest.getContentType());
        httpResponse.setResponseBody(responseBody);
        httpResponse.setContentLength(responseBody.getBytes().length);

        return httpResponse;
    }

    private Session register(HttpRequest httpRequest) {
        String requestBody = httpRequest.getRequestBody();

        String account = requestBody.split("&")[0].split("=")[1];
        String password = requestBody.split("&")[1].split("=")[1];
        String email = requestBody.split("&")[2].split("=")[1];

        InMemoryUserRepository.save(new User(account, password, email));
        return AuthManager.authenticate(requestBody);
    }

    private String getResource(String path) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + path);
        File file = new File(resource.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
