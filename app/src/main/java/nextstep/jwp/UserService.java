package nextstep.jwp;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class UserService {

    public UserService() {
    }

    public Optional<User> findUserFromBody(String requestBody) {
        Map<String, String> loginData = extractUserDataFromRequestBody(requestBody);
        String account = loginData.getOrDefault("account", null);
        String password = loginData.getOrDefault("password", null);

        Optional<User> user = InMemoryUserRepository.findByAccountAndPassword(account, password);

        return user;
    }

    public void saveUser(String requestBody) {
        Map<String, String> registerData = extractUserDataFromRequestBody(requestBody);

        InMemoryUserRepository.save(new User(InMemoryUserRepository.autoIncrementId,
                registerData.get("account"),
                registerData.get("password"),
                registerData.get("email")));
    }

    private Map<String, String> extractUserDataFromRequestBody(String requestBody) {
        Map<String, String> extractData = new HashMap<>();
        String[] splitRequestBody = requestBody.split("=|&");

        for (int i = 0; i < splitRequestBody.length; i += 2) {
            extractData.put(splitRequestBody[i], splitRequestBody[i + 1]);
        }
        return extractData;
    }
}
