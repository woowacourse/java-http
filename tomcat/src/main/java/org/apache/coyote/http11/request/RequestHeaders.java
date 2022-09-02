package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHeaders {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String ACCEPT = "Accept";
    private static final String QUERY_STRING_BEGIN_DELIMITER = "?";
    private static final String QUERY_STRING_KEY_VALUE_DELIMITER = "=";
    private static final String QUERY_STRING_UNIT_DELIMITER = "&";


    private final Map<String, String> requestHeaders;

    public RequestHeaders(Map<String, String> requestHeaders, String requestUri) {
        this.requestHeaders = requestHeaders;

        if (issQueryStringExist(requestUri)) {
            validateUser(requestUri);
        }
    }

    private boolean issQueryStringExist(String requestUri) {
        return requestUri.contains(QUERY_STRING_BEGIN_DELIMITER);
    }

    private void validateUser(String requestUri) {
        Map<String, String> queryString = getQueryString(requestUri);
        if (queryString.containsKey("account")) {
            User existedUser = InMemoryUserRepository.findByAccount(queryString.get("account"))
                    .orElseThrow(() -> new IllegalArgumentException("아이디가 일지하지 않는다."));
            if (!existedUser.checkPassword(queryString.get("password"))) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않는다.");
            }
            log.info(existedUser.toString());
        }
    }

    public Map<String, String> getQueryString(String requestUri) {
        int index = requestUri.indexOf(QUERY_STRING_BEGIN_DELIMITER);
        String queryString = requestUri.substring(index + 1);
        String[] dividedQueryString = queryString.split(QUERY_STRING_UNIT_DELIMITER);
        Arrays.stream(dividedQueryString).forEach(System.out::println);

        return Arrays.stream(dividedQueryString)
                .map(query -> query.split(QUERY_STRING_KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(key -> key[0], value -> value[1]));
    }

    public String getAcceptHeaderValue() {
        return requestHeaders.get(ACCEPT);
    }

    private boolean isExistAccept() {
        return requestHeaders.containsKey(ACCEPT);
    }

    public boolean isAcceptValueCss() {
        return isExistAccept() && (requestHeaders.get(ACCEPT).contains("text/css"));
    }
}
