package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.dto.RegisterInfo;
import com.techcourse.model.User;
import org.apache.coyote.util.StringUtils;

import java.util.Map;

public class LoginService {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    public void register(String body) {
        Map<String, String> parsedBody = StringUtils.separateKeyValue(body);
        RegisterInfo registerInfo = new RegisterInfo(
                parsedBody.get(ACCOUNT), parsedBody.get(PASSWORD), parsedBody.get(EMAIL));
        InMemoryUserRepository.save(new User(
                registerInfo.account(), registerInfo.password(), registerInfo.email()));
    }
}
