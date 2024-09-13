package org.apache.coyote.http11.request;

public class Http11RequestTarget {

    private static final int ENDPOINT_INDEX = 0;
    private static final int QUERY_INDEX = 1;
    private static final String QUERY_DELIMITER = "?";
    private static final String QUERY_DELIMITER_REGEX = "\\?";

    private final String endPoint;
    private final QueryStrings queryStrings;

    private Http11RequestTarget(String endPoint, QueryStrings queryStrings) {
        this.endPoint = endPoint;
        this.queryStrings = queryStrings;
    }

    public static Http11RequestTarget from(String value) {
        if (value.contains(QUERY_DELIMITER)) {
            String[] values = value.split(QUERY_DELIMITER_REGEX);
            return new Http11RequestTarget(values[ENDPOINT_INDEX], QueryStrings.from(values[QUERY_INDEX]));
        }
        return new Http11RequestTarget(value, QueryStrings.from(""));
    }

    public String getParam(String key) {
        return queryStrings.getQuery(key);
    }

    public String getEndPoint() {
        return endPoint;
    }

    @Override
    public String toString() {
        return "RequestTarget{" +
               "endPoint='" + endPoint + '\'' +
               ", queryStrings=" + queryStrings +
               '}';
    }
}
