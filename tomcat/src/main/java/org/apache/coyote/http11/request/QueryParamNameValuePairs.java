package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.NameValuePairs;

public class QueryParamNameValuePairs extends NameValuePairs {

    private static final String QUERY_PAIR_DELIMITER = "&";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";

    public QueryParamNameValuePairs(String nameAndValue) {
        super(nameAndValue, QUERY_PAIR_DELIMITER, QUERY_KEY_VALUE_DELIMITER);
    }
}
