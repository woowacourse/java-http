package org.apache.coyote.http11.request.resolver;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.RequestBody;

public class UserResolver<T> implements RequestBodyResolver<User> {

    private static final String USER_REGISTRATION_INFO_DELIMITER = "&";
    private static final String INFO_ELEMENT_DELIMITER = "=";
    private static final int ELEMENT_KEY_INDEX = 0;
    private static final int ELEMENT_VALUE_INDEX = 1;
    private static final String USER_ACCOUNT_KEY = "account";
    private static final String USER_PASSWORD_KEY = "password";
    private static final String USER_EMAIL_KEY = "email";

    @Override
    public User resolve(RequestBody requestBody) {
        Map<String, String> registerInfo = new HashMap<>();
        String body = requestBody.getContent();
        String[] elements = body.split(USER_REGISTRATION_INFO_DELIMITER);

        for (String element : elements) {
            String[] parsedElement = element.split(INFO_ELEMENT_DELIMITER);
            registerInfo.put(parsedElement[ELEMENT_KEY_INDEX], parsedElement[ELEMENT_VALUE_INDEX]);
        }

        return new User(
                registerInfo.get(USER_ACCOUNT_KEY),
                registerInfo.get(USER_PASSWORD_KEY),
                registerInfo.get(USER_EMAIL_KEY)
        );
    }
}
