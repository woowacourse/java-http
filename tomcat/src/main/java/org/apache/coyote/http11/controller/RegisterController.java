package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class RegisterController extends AbstractController {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String method = request.getMethod();

        if (method.equalsIgnoreCase("GET")) {
            doGet(request, response);
            return;
        } else if (method.equalsIgnoreCase("POST")) {
            doPost(request, response);
            return;
        }
        throw new IllegalArgumentException("Method not supported: " + method);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        response.setFileType("html");
        response.setHttpStatusCode(HttpStatusCode.OK);
        response.setResponseBody(responseBody);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, List<String>> body = request.getBody();
        String account = body.get("account").getFirst();
        String password = body.get("password").getFirst();
        String email = body.get("email").getFirst();
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.setPath("/index.html");
        response.setFileType("html");
        final URL resource = getClass().getClassLoader().getResource("static" + response.getPath());
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found");
        }
        
        final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        response.setResponseBody(responseBody);
        response.setHttpStatusCode(HttpStatusCode.FOUND);
    }
}
