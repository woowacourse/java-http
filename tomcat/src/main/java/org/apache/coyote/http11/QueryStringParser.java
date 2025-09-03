package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryStringParser {

    public static final String QUERY_PARAM_STARTER = "?";
    private static final String PARAM_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String HTML_EXTENSION = ".html";

    private static final Logger log = LoggerFactory.getLogger(QueryStringParser.class);

    public static String parseAndProcessQuery(String requestUri) {
        String resource = requestUri;
        int queryParamIndex = requestUri.indexOf(QUERY_PARAM_STARTER);
        if (queryParamIndex != -1) {
            resource = requestUri.substring(0, queryParamIndex) + HTML_EXTENSION;
            String queryString = requestUri.substring(queryParamIndex + 1);

            Map<String, String> queryParams = parseQueryString(queryString);
            authenticateUser(queryParams);
        }
        return resource;
    }

    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        String[] paramTokens = queryString.split(PARAM_SEPARATOR);
        for (String paramPair : paramTokens) {
            String[] keyValue = paramPair.split(KEY_VALUE_SEPARATOR);
            queryParams.put(keyValue[0], keyValue[1]);
        }
        return queryParams;
    }
    
    private static void authenticateUser(Map<String, String> queryParams) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저 정보입니다."));
        if (user.checkPassword(password)) {
            log.info(user.toString());
        }
    }
}
