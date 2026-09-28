package org.apache.coyote.login;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginParser {

    private static final Logger log = LoggerFactory.getLogger(LoginParser.class);


    public static Map<String, String> parseQueryString(String queryString) {

        if (queryString == null) {
            log.error("[parseQueryString] queryString이 null입니다.");
            throw new IllegalStateException();
        }

        final Map<String, String> result = new HashMap<>();

        final String[] queries = queryString.split("&");
        for (String query : queries) {
            final String[] keyValue = query.split("=", 2);
            if (keyValue.length != 2) {
                log.error("[authenticateUser] query의 형식이 올바르지 않습니다. query = {}", query);
                throw new IllegalStateException();
            }

            String queryKey = URLDecoder.decode(keyValue[0], UTF_8);
            String queryValue = URLDecoder.decode(keyValue[1], UTF_8);

            log.debug("[parseQueryString] account = {}, password = {}", queryKey, queryValue);

            result.put(queryKey, queryValue);
        }

        return result;
    }
}
