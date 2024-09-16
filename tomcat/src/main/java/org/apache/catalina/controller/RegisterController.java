package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.ResourceManager;
import org.apache.catalina.AuthManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import java.io.IOException;

import static org.apache.catalina.AuthManager.AUTHENTICATION_COOKIE_NAME;
import static org.apache.coyote.http11.Status.FOUND;
import static org.apache.coyote.http11.Status.OK;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        HttpResponse httpResponse = new HttpResponse();

        Session session = register(httpRequest);

        httpResponse.setStatusLine(FOUND);
        httpResponse.setCookie(AUTHENTICATION_COOKIE_NAME, session.getId());
        httpResponse.setLocation("/index.html");

        return httpResponse;
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        HttpResponse httpResponse = new HttpResponse();

        String responseBody = ResourceManager.getFileResource(httpRequest.getPath());

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
}
