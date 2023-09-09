package org.apache.coyote.http11.httpmessage.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryString {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String QUERY_STRING_DELIMITER = "=";

    private final Map<String, String> queryStrings;

    private QueryString(final Map<String, String> queryStrings) {
        this.queryStrings = queryStrings;
    }

    public static QueryString fromRequest(final String[] queryString) {
        final Map<String, String> queryStrings = Arrays.stream(queryString)
            .map(headerContent -> headerContent.split(QUERY_STRING_DELIMITER))
            .collect(Collectors.toMap(
                headerContent -> headerContent[KEY_INDEX],
                headerContent -> headerContent[VALUE_INDEX]));
        return new QueryString(queryStrings);
    }

    public String getAccount() {
        return queryStrings.get("account");
    }

    public String getPassword() {
        return queryStrings.get("password");
    }

    public Map<String, String> getQueryString() {
        return queryStrings;
    }
}
