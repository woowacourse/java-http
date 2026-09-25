package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController extends AbstractController {
    private static final String INDEX_PAGE = "/index.html";

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    public RegisterController() {
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        if (request.requestHeader().getContentLength().isEmpty()) {
            return HttpResponse.status(HttpStatus.LENGTH_REQUIRED);
        }

        return registerUser(request.requestBody());
    }

    private HttpResponse registerUser(HttpRequestBody body) {
        Map<String, String> formData = body.formData();

        String account = formData.get("account");
        String email = formData.get("email");
        String password = formData.get("password");

        if (account.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return HttpResponse.status(HttpStatus.BAD_REQUEST);
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        log.info("new user : {}", user);

        return HttpResponse.found()
                .location(INDEX_PAGE);
    }
}
