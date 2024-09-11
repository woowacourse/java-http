package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Http11RequestBody;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.HttpStatusCode;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(Http11Request request, Http11Response response) throws Exception {
        Http11RequestBody requestBody = request.getRequestBody();
        User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);

        response.setStatusCode(HttpStatusCode.FOUND);
        response.addHeader("Location", "/index.html");
    }

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        super.doGet(request, response);
    }
}
