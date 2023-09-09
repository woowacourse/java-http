package org.apache.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.controller.FileReader.FileReader;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    protected void doGet(Request request, Response response) {
        FileReader fileReader = FileReader.from(request.getPath());
        String body = fileReader.read();

        response.setHttpStatus(HttpStatus.OK);
        response.addHeaders(CONTENT_TYPE, request.getResourceTypes());
        response.addHeaders(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        response.setResponseBody(body);
    }

    @Override
    protected void doPost(Request request, Response response) {
        Map<String, String> body = request.getBody();
        String account = body.get(ACCOUNT);
        String password = body.get(PASSWORD);
        String email = body.get(EMAIL);
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return;
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.setHttpStatus(HttpStatus.FOUND);
        response.redirectLocation("/index.html");
    }
}
