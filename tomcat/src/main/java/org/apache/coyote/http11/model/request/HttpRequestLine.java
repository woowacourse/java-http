package org.apache.coyote.http11.model.request;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestLine {

    private static final String EXIST_QUERY_PARAMS = "?";
    private static final String QUERY_PARAMS_DELIMITER = "&";
    private static final String QUERY_PRAM_VALUE_DELIMITER = "=";
    private static final int QUERY_PRAM_KEY_INDEX = 0;
    private static final int QUERY_PARAM_VALUE_INDEX = 1;

    private final Method method;
    private final String target;
    private final Map<String, String> params;

    private HttpRequestLine(final Method method, final String target, final Map<String, String> params) {
        this.method = method;
        this.target = target;
        this.params = params;
    }

    public static HttpRequestLine of(final String requestLine) {
        String[] splitRequestLine = requestLine.split(" ");
        Method method = Method.findMethod(splitRequestLine[0]);
        String target = createTarget(splitRequestLine[1]);
        Map<String, String> params = createParams(splitRequestLine[1]);
        return new HttpRequestLine(method, target, params);
    }

    private static String createTarget(final String input) {
        if (existQueryParams(input)) {
            return input;
        }
        return input.substring(0, input.lastIndexOf(EXIST_QUERY_PARAMS));
    }

    private static Map<String, String> createParams(final String input) {
        Map<String, String> queryParams = new HashMap<>();
        if (existQueryParams(input)) {
            return queryParams;
        }
        return splitQueryParams(input, queryParams);
    }

    private static Map<String, String> splitQueryParams(final String path, final Map<String, String> queryParams) {
        String[] queryString = getQueryString(path);
        for (String param : queryString) {
            String[] paramsInfo = param.split(QUERY_PRAM_VALUE_DELIMITER);
            queryParams.put(paramsInfo[QUERY_PRAM_KEY_INDEX], paramsInfo[QUERY_PARAM_VALUE_INDEX]);
        }
        return queryParams;
    }

    private static String[] getQueryString(final String path) {
        String queryString = path.substring(path.lastIndexOf(EXIST_QUERY_PARAMS) + 1);
        return queryString.split(QUERY_PARAMS_DELIMITER);
    }

    private static boolean existQueryParams(final String path) {
        return !path.contains(EXIST_QUERY_PARAMS);
    }

    public boolean isEmptyQueryParams() {
        return params.isEmpty();
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isGetMethod() {
        return method == Method.GET;
    }

    public boolean matchMethod(final Method method) {
        return this.method == method;
    }

    @Override
    public String toString() {
        return "HttpRequestLine{" +
                "method=" + method.getName() +
                ", target='" + target + '\'' +
                ", params=" + params +
                '}';
    }

    public boolean hasParams() {
        return params.isEmpty();
    }
}
