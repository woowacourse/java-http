package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.domain.controller.AbstractController;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.request.RequestBody;
import org.apache.coyote.http11.domain.response.HttpResponse;

public class RegisterController extends AbstractController {

    private static final String QUESTION_MARK = "?";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private final UserService userService;


    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.redirect("/register.html").build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        RequestBody requestBody = request.getRequestBody();

        String account = requestBody.get("account");
        String email = requestBody.get("email");
        String password = requestBody.get("password");

        if (account == null || email == null || password == null) {
            return HttpResponse.redirect("/register.html").build();
        }

        userService.register(account, email, password);

        return HttpResponse.redirect("/index.html").build();
    }

    private Map<String, String> parseQuery(String requestBody) {
        return Arrays.stream(requestBody.split(PARAMETER_DELIMITER))
                .filter(param -> param.contains(KEY_VALUE_DELIMITER))
                .map(param -> param.split(KEY_VALUE_DELIMITER))
                .filter(param -> param.length == 2)
                .filter(param -> !param[0].isEmpty())
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(param -> param[0],
                                param -> param[1]
                        ), HashMap::new));
    }
}
