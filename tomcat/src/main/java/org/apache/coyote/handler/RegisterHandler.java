package org.apache.coyote.handler;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.FileReader.FileReader;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.HttpStatus;
import org.apache.coyote.response.Response;

public class RegisterHandler {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    public void register(Request request, Response response) {
        if (request.isSameHttpMethod(HttpMethod.POST)) {
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
        if (request.isSameHttpMethod(HttpMethod.GET)) {
            String path = request.getPath();
            FileReader fileReader = FileReader.from(path);
            String body = fileReader.read();
            response.setHttpStatus(HttpStatus.OK);
            response.addHeaders("Content-Type", request.getResourceTypes());
            response.addHeaders("Content-Length", String.valueOf(body.getBytes().length));
            response.setResponseBody(body);
        }
    }
}
