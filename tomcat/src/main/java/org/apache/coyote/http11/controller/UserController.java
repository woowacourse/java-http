package org.apache.coyote.http11.controller;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.service.UserService;

public class UserController implements Controller {

    private static final String USER_REGISTRATION_INFO_DELIMITER = "&";
    private static final String INFO_ELEMENT_DELIMITER = "=";
    private static final int ELEMENT_KEY_INDEX = 0;
    private static final int ELEMENT_VALUE_INDEX = 1;

    private final UserService userService = UserService.getInstance();

    @Override
    public boolean canHandle(String url) {
        return url.contains("register");
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.isMethod(HttpMethod.POST)) {
            User user = resolveUser(httpRequest.getRequestBody());
            userService.save(user);

            httpResponse.statusCode(HttpStatusCode.FOUND_302)
                    .location("/index.html");
            return;
        }

        httpResponse.statusCode(HttpStatusCode.OK_200)
                .viewUrl("/register.html");
    }

    private User resolveUser(RequestBody requestBody) {
        Map<String, String> registerInfo = new HashMap<>();
        String body = requestBody.getContent();
        String[] elements = body.split(USER_REGISTRATION_INFO_DELIMITER);

        for (String element : elements) {
            String[] parsedElement = element.split(INFO_ELEMENT_DELIMITER);
            registerInfo.put(parsedElement[ELEMENT_KEY_INDEX], parsedElement[ELEMENT_VALUE_INDEX]);
        }

        return new User(
                registerInfo.get("account"),
                registerInfo.get("password"),
                registerInfo.get("email")
        );
    }
}
