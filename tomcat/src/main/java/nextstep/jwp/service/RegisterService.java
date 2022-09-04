package nextstep.jwp.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterService {

    private static final String SUCCESS_URL = "/index.html";
    private static final String FAIL_URL = "/401.html";

    private static final RegisterService registerService = new RegisterService();

    private RegisterService() {
    }

    public static String signUp(String account, String password, String email) {
        return registerService.signUpInternal(account, password, email);
    }

    public String signUpInternal(String account, String password, String email) {
        try {
            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);
            return SUCCESS_URL;
        }
        catch (IllegalArgumentException e) {
            return FAIL_URL;
        }
    }
}
