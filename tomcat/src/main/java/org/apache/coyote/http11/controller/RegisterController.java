package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestBody;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.ResourceLoader;

public class RegisterController extends AbstractController {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        super.service(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        try {
            String path = request.getResourcePath();
            response.write(ResourceLoader.findResource(path));
            response.setStatusCode(HttpStatusCode.OK);
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
            HttpRequestBody body = request.getBody();
            Map<String, String> formData = body.getFormData();

            String account = formData.get("account");
            String password = formData.get("password");
            String email = formData.get("email");

            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            String path = request.getUri().getPath();

            response.setStatusCode(HttpStatusCode.FOUND);
            response.setMimeType(ResourceLoader.getMimeType(path));

            response.sendRedirect("/login");
        } catch (IllegalArgumentException e) {
            response.setStatusCode(HttpStatusCode.FOUND);
            response.sendRedirect("/401.html");
        }
    }
}
