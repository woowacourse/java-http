package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class QueryParser {

    private static final Logger log = LoggerFactory.getLogger(QueryParser.class);
    private static final String AMPERSAND = "&";
    private static final String EQUAL_SIGN = "=";
    private static final Map<String, String> query = new HashMap<>();

    private QueryParser() {
    }

    public static void parse(String path, int index) {
        String queryString = path.substring(index + 1);
        String[] queries = queryString.split(AMPERSAND);
        for (String q : queries) {
            String[] keyValue = q.split(EQUAL_SIGN);
            query.put(keyValue[0], keyValue[1]);
        }
        logLogin();
    }

    private static void logLogin(){
        InMemoryUserRepository.findByAccount(query.get("account"))
                .filter(user -> user.checkPassword(query.get("password")))
                .ifPresentOrElse(user -> log.info("로그인 성공"), () -> log.info("로그인 실패"));
    }
}
