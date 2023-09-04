package org.apache.coyote.http11.httpmessage.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryString {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String QUERY_STRING_DELIMITER = "=";

    private final Map<String, String> queryString;

    private QueryString(final Map<String, String> queryString) {
        this.queryString = queryString;
    }

    public static QueryString fromRequest(final String[] queryStrings) {
        final Map<String, String> queryString = Arrays.stream(queryStrings)
            .map(headerContent -> headerContent.split(QUERY_STRING_DELIMITER))
            .collect(Collectors.toMap(
                headerContent -> headerContent[KEY_INDEX],
                headerContent -> headerContent[VALUE_INDEX]));
        return new QueryString(queryString);
    }

    public String getAccount() {
        return queryString.get("account");
    }

    public String getPassword() {
        return queryString.get("password");
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }
}
