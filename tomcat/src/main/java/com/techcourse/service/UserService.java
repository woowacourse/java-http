package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.UUID;
import org.apache.coyote.http11.HttpRequestParameter;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public static void createUser(HttpRequestParameter requestParameter) {
        String account = requestParameter.getValue("account");
        String password = requestParameter.getValue("password");
        String email = requestParameter.getValue("email");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    public static String login(HttpRequestParameter requestParameter) {
        String account = requestParameter.getValue("account");
        String password = requestParameter.getValue("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("account에 해당하는 사용자가 없습니다."));
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        log.info("user: " + user);

        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        session.setAttribute("account", account);
        SessionManager.add(session);
        return sessionId;
    }
}
