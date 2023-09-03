package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.exception.InvalidRequestLineException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestURL {
    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String NON_RESERVED_QUERY_PARAM_DELIMITER = "\\?";
    private static final String NON_RESERVED_EXTENSION_DELIMITER = "\\.";
    private static final String DEFAULT_EXTENSION = ".html";
    private static final String EXTENSION_DELIMITER = ".";
    private static final String SPACE_DELIMITER = " ";

    private final HttpMethod method;
    private final String url;
    private final String version;
    private final Map<String, String> requestParam;

    private RequestURL(final HttpMethod method, final String url, final String version, final Map<String, String> requestParam) {
        this.method = method;
        this.url = url;
        this.version = version;
        this.requestParam = requestParam;
    }

    public static RequestURL from(String line) {
        validate(line);

        final String[] splitedLine = line.split(SPACE_DELIMITER);

        return new RequestURL(getMethod(splitedLine[METHOD_INDEX]), splitedLine[URL_INDEX],
                splitedLine[VERSION_INDEX], parseQueryParam(splitedLine[URL_INDEX]));
    }

    private static void validate(final String line) {
        if (line.isBlank() && line.isEmpty()) {
            throw new InvalidRequestLineException();
        }
    }

    private static Map<String, String> parseQueryParam(String url) {
        if (!url.contains(QUERY_STRING_DELIMITER)) {
            return Collections.emptyMap();
        }
        final String[] splitedQueryString = url.split(NON_RESERVED_QUERY_PARAM_DELIMITER)[1].split(QUERY_PARAM_DELIMITER);
        Map<String, String> queryStrings = new HashMap<>();
        for (String queryString : splitedQueryString) {
            final String[] splitedQuery = queryString.split(KEY_VALUE_DELIMITER);
            queryStrings.put(splitedQuery[0], splitedQuery[1]);
        }

        return queryStrings;
    }

    public String getAbsolutePath() {
        String absolutePath = url;
        if (absolutePath.contains(QUERY_STRING_DELIMITER)) {
            absolutePath = absolutePath.split(NON_RESERVED_QUERY_PARAM_DELIMITER)[0];
        }
        if (!absolutePath.contains(EXTENSION_DELIMITER)) {
            absolutePath += DEFAULT_EXTENSION;
        }
        return absolutePath;
    }

    public String getExtension() {
        final String[] splitedUrl = url.split(NON_RESERVED_EXTENSION_DELIMITER);
        if (splitedUrl.length > 1) {
            return splitedUrl[splitedUrl.length - 1];
        }
        return "html";
    }

    private static HttpMethod getMethod(final String method) {
        return HttpMethod.valueOf(method);
    }

    public Map<String, String> getRequestParam() {
        return requestParam;
    }

    public String getUrl() {
        return url;
    }
}
