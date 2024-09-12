package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RequestBody {
    private static final String ACCOUNT_FIELD = "account";
    private static final String PASSWORD_FIELD = "password";

    private String body;

    public RequestBody() {
        this.body = "";
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getUser() {
        Map<String, String> userInformation = getUserInformation();
        if (!userInformation.containsKey(ACCOUNT_FIELD) || !userInformation.containsKey(PASSWORD_FIELD)) {
            return null;
        }

        String account = userInformation.get(ACCOUNT_FIELD);
        String password = userInformation.get(PASSWORD_FIELD);

        return Optional.ofNullable(InMemoryUserRepository.findByAccount(account))
                .filter(user -> user.checkPassword(password))
                .orElse(null);
    }

    public Map<String, String> getUserInformation() {
        return Arrays.stream(body.split("&"))
                .map(line -> line.split("="))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1]
                ));
    }
}
