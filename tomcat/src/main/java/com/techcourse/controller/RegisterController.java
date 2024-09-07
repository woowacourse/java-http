package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.MimeType;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseHeader;

public class RegisterController implements Controller {

    private static final String ACCOUNT_KEY = "account";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";

    private final InMemoryUserRepository userRepository;

    public RegisterController() {
        userRepository = new InMemoryUserRepository();
    }

    @Override
    public HttpResponse service(HttpRequest request, Manager manager) {
        String body = request.getBody();
        Map<String, String> parsedBody = parseBody(body);

        String account = parsedBody.get(ACCOUNT_KEY);
        String email = parsedBody.get(EMAIL_KEY);
        String password = parsedBody.get(PASSWORD_KEY);

        userRepository.save(new User(account, password, email));

        return redirectDefaultPage();
    }

    private Map<String, String> parseBody(String query) {
        Map<String, String> result = new HashMap<>();
        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            String key = keyValue[0];
            String value = keyValue[1];
            result.put(key, value);
        }

        return result;
    }

    private HttpResponse redirectDefaultPage() {
        ResponseHeader header = new ResponseHeader();
        header.setLocation("/index.html");
        header.setContentType(MimeType.HTML);
        return new HttpResponse(HttpStatusCode.FOUND, header);
    }
}
