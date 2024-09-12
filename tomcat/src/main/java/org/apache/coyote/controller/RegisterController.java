package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http.HeaderName;
import org.apache.coyote.http.StatusCode;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        RequestBody requestBody = request.getBody();
        String account = requestBody.get("account");    // TODO: DTO
        String password = requestBody.get("password");
        String email = requestBody.get("email");
        InMemoryUserRepository.save(new User(account, password, email));

        response.setStatusCode(StatusCode.FOUND);
        response.addHeader(HeaderName.LOCATION, "/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatusCode(StatusCode.OK);
        response.setBody("/register.html");
    }
}
