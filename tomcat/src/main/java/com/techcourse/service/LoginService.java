package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.dto.LoginInfo;
import com.techcourse.dto.RegisterInfo;
import com.techcourse.model.User;
import org.apache.coyote.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LoginService {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public void register(String body) {
        Map<String, String> parsedBody = parseInfo(body);
        RegisterInfo registerInfo = new RegisterInfo(
                parsedBody.get(ACCOUNT), parsedBody.get(PASSWORD), parsedBody.get(EMAIL));
        InMemoryUserRepository.save(new User(
                registerInfo.account(), registerInfo.password(), registerInfo.email()));
    }

    public User login(String body) {
        Map<String, String> parsedBody = parseInfo(body);
        LoginInfo loginInfo = new LoginInfo(
                parsedBody.get(ACCOUNT), parsedBody.get(PASSWORD));
        User user = InMemoryUserRepository.findByAccount(loginInfo.account())
                .orElseThrow(() -> new IllegalArgumentException("Account not found or wrong password"));
        if (!user.checkPassword(loginInfo.password())) {
            throw new IllegalArgumentException("Account not found or wrong password");
        }
        log.info("Logged in user: {}", user);
        return user;
    }

    private Map<String, String> parseInfo(String body) {
        return StringUtils.separateKeyValue(body);
    }
}
