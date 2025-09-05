package com.techcourse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service {

    private static final Logger log = LoggerFactory.getLogger(Service.class);

    public byte[] findUser(Map<String, String> params) {
        User user = findUserByAccount(params.get("account"));

        log.info("Found user: " + user.toString());

        return String.join("\r\n",
                "{",
                "account: " + user.getAccount(),
                "}").getBytes();
    }

    private User findUserByAccount(final String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}
