package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.StaticResource;
import org.apache.coyote.http11.StaticResourceProvider;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(Http11Request request, Http11Response response) throws Exception {
        final StaticResource staticResource = StaticResourceProvider.getStaticResource("/register.html");
        response.setStaticResource(staticResource);
        response.setHttpStatus(HttpStatus.OK);
    }

    @Override
    protected void doPost(Http11Request request, Http11Response response) throws Exception {
        final String account = request.getBodyParam("account");
        final String email = request.getBodyParam("email");
        final String password = request.getBodyParam("password");

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.setHttpStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
    }
}
