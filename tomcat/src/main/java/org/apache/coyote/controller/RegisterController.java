package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponseStatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Override
    public HttpResponse process(HttpRequest request) {
        String account = request.body().getAttribute("account");
        String email = request.body().getAttribute("email");
        String password = request.body().getAttribute("password");

        InMemoryUserRepository.save(new User(account, password, email));
        log.info("Register success: { account: {}, email: {}, password:{}}", account, email, password);

        return new HttpResponse(
                new HttpResponseStatusLine(302, "Found"),
                Map.of("Location", "/index.html"),
                null
        );
    }
}
