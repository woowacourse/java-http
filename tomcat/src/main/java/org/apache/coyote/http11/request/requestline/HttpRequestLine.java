package org.apache.coyote.http11.request.requestline;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpRequestLine {
    public static final String QUERY_DELIMITER = "=";
    public static final String REQUEST_LINE_DELIMITER = " ";
    public static final int REQUEST_LINE_ARGUMENT_COUNT = 3;

    private final HttpMethod method;
    private final HttpLocation location;
    private final HttpVersion version;
    private final Map<String, String> queries = new TreeMap<>();

    private HttpRequestLine(String method, String location, String version) {
        this.method = HttpMethod.from(method);
        this.location = HttpLocation.from(location);
        this.version = HttpVersion.from(version);
        setQueriesIfExist(location);
    }

    private void setQueriesIfExist(String location) {
        String[] locationAndQuery = location.split("\\?");
        if (isNotParamQueryFormat(locationAndQuery)) {
            return;
        }

        for (int i = 1; i < locationAndQuery.length; i++) {
            String[] keyValue = locationAndQuery[i].split(QUERY_DELIMITER);
            String queryKey = keyValue[0];
            String queryValue = keyValue[1];
            queries.put(queryKey, queryValue);
        }
    }

    private boolean isNotParamQueryFormat(String[] locationAndQuery) {
        return locationAndQuery.length == 1;
    }

    public static HttpRequestLine from(List<String> clientData) {
        if (clientData.isEmpty()) {
            throw new IllegalArgumentException("data parse error at request line");
        }

        String requestLine = clientData.getFirst();
        List<String> requestLineArguments = Arrays.stream(requestLine.split(REQUEST_LINE_DELIMITER))
                .toList();

        if (requestLineArguments.size() != REQUEST_LINE_ARGUMENT_COUNT) {
            throw new IllegalArgumentException("request line must have " + REQUEST_LINE_DELIMITER + "arguments");
        }

        String method = requestLineArguments.get(0);
        String location = requestLineArguments.get(1);
        String version = requestLineArguments.get(2);
        return new HttpRequestLine(method, location, version);

    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpLocation getLocation() {
        return location;
    }

    public Map<String, String> getQueries() {
        return queries;
    }
}
