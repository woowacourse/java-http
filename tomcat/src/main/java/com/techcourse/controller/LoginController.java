package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    @Override
    public void service(HttpRequest req) {
        Map<String, String> requestQueries = req.getQueries();

        String account = "";
        String password = "";
        for (Entry<String, String> entry : requestQueries.entrySet()) {
            if (entry.getKey().equals(ACCOUNT)) {
                account = entry.getValue();
            } else if (entry.getKey().equals(PASSWORD)) {
                password = entry.getValue();
            }
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("찾으시는 유저가 존재하지 않습니다."));
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("찾으시는 유저가 존재하지 않습니다.");
        }

        log.info(user.toString());
    }
}
