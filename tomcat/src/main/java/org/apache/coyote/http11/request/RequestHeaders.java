package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.LoginFailureException;
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

        if (isQueryStringExist(requestUri)) {
            validateAccount(requestUri);
        }
    }

    private boolean isQueryStringExist(String requestUri) {
        return requestUri.contains(QUERY_STRING_BEGIN_DELIMITER);
    }

    private void validateAccount(String requestUri) {
        Map<String, String> queryString = getQueryString(requestUri);
        if (queryString.containsKey("account")) {
            User existedUser = findUser(queryString.get("account"), queryString.get("password"));
            log.info(existedUser.toString());
        }
    }

    private User findUser(String account, String password) {
        User existedUser = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(LoginFailureException::new);
        if (!existedUser.checkPassword(password)) {
            throw new LoginFailureException();
        }
        return existedUser;
    }

    private Map<String, String> getQueryString(String requestUri) {
        int index = requestUri.indexOf(QUERY_STRING_BEGIN_DELIMITER);
        String queryString = requestUri.substring(index + 1);
        String[] dividedQueryString = queryString.split(QUERY_STRING_UNIT_DELIMITER);
        Arrays.stream(dividedQueryString).forEach(System.out::println);

        return Arrays.stream(dividedQueryString)
                .map(query -> query.split(QUERY_STRING_KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(key -> key[0].trim(), value -> value[1].trim()));
    }

    public String getAcceptHeaderValue() {
        if(isExistAccept()) {
            return requestHeaders.get(ACCEPT);
        }
        throw new IllegalArgumentException("header에 Accept 필드가 없습니다.");
    }

    private boolean isExistAccept() {
        return requestHeaders.containsKey(ACCEPT);
    }

    public boolean isAcceptValueCss() {
        return isExistAccept() && (requestHeaders.get(ACCEPT).contains("text/css"));
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }
}
