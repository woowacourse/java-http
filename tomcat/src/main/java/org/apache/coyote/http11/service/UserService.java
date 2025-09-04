package org.apache.coyote.http11.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.parser.ContentParseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class UserService implements HttpService {

    private static final Logger log = LoggerFactory.getLogger(HttpService.class);

    public ContentParseResult doRequest(Map<String, String> query) {
        User user = InMemoryUserRepository.findByAccount(query.get("account"))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        log.info(user.toString());
        return null;
    }
}
