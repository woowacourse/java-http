package org.apache.coyote.http11.request;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.LoginParam;
import com.techcourse.model.User;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class UserRegisterManager {

    private final Map<LoginParam, String> userInformation;

    public static UserRegisterManager of(String requestBody) {
        Map<LoginParam, String> userInformation = parseRequestBody(requestBody);

        return new UserRegisterManager(userInformation);
    }

    private UserRegisterManager(Map<LoginParam, String> userInformation) {
        this.userInformation = userInformation;
    }

    public boolean existsUserByAccount() {
        return InMemoryUserRepository.existsByAccount(userInformation.get(LoginParam.ACCOUNT));
    }

    public void saveUser() {
        String account = userInformation.get(LoginParam.ACCOUNT);
        String email = userInformation.get(LoginParam.EMAIL);
        String password = userInformation.get(LoginParam.PASSWORD);
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    public User getUser() {
        return InMemoryUserRepository.findByAccount(userInformation.get(LoginParam.ACCOUNT)).
                orElseThrow(() -> new IllegalArgumentException("[ERROR] no such user"));
    }

    public boolean isExistsUser() {
        String account = userInformation.get(LoginParam.ACCOUNT);

        return InMemoryUserRepository.existsByAccount(account);
    }

    public boolean isPasswordCorrect() {
        String password = userInformation.get(LoginParam.PASSWORD);

        return getUser().isPasswordCorrect(password);
    }

    private static Map<LoginParam, String> parseRequestBody(String requestBody) {
        Map<LoginParam, String> userInformation = new HashMap<>();
        String[] keyValuePairs = requestBody.split("&");

        for (String pair : keyValuePairs) {
            validateContainsEqual(pair);

            String[] keyValue = pair.split("=");
            String key = keyValue[0];
            String encodedValue = keyValue[1];

            LoginParam loginParam = LoginParam.getLoginParam(key);
            String decodedValue = URLDecoder.decode(encodedValue, UTF_8);

            userInformation.put(loginParam, decodedValue);
        }

        return userInformation;
    }

    private static void validateContainsEqual(String query) {
        if (!query.contains("=")) {
            throw new IllegalArgumentException("[ERROR] invalid query format");
        }
    }
}
