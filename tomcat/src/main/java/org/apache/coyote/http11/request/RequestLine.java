package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestLine {

    private static final int REQUEST_LINE_COMPONENT = 3;
    private static final String INVIDUAL_QUERY_PARAM_DIVIDER = "&";
    private static final String QUERY_PARAM_KEY_VALUE_SPLIT = "=";
    private static final int DONT_HAVE_VALUE = 1;
    private static final String QUERY_PARAM_SPLITER = "\\?";
    private static final String QUERY_PARAM = "?";

    private final Method method;
    private final String path;
    private final Map<String, String> queryParam;
    private final String httpVersion;

    public RequestLine(String requestLine) {
        String[] split = requestLine.split(" ");
        validate(split);
        this.method = methodOf(split[0]);
        this.path = pathOf(split[1]);
        this.queryParam = queryParamsOf(split[1]);
        this.httpVersion = split[2];
    }

    private void validate(String[] split) {
        if (split.length != REQUEST_LINE_COMPONENT) {
            throw new IllegalArgumentException("http 요청 라인은 3가지 요소로 구성되어야 합니다.");
        }
    }

    private Method methodOf(String methodName) {
        return Method.of(methodName);
    }

    private String pathOf(String requestTarget) {
        String[] split = requestTarget.split(QUERY_PARAM_SPLITER);
        return split[0];
    }

    private Map<String, String> queryParamsOf(String requestTarget) {
        if (!requestTarget.contains(QUERY_PARAM)) {
            return Collections.emptyMap();
        }
        String[] split = requestTarget.split(QUERY_PARAM_SPLITER);
        return Stream.of(split[1].split(INVIDUAL_QUERY_PARAM_DIVIDER))
            .collect(Collectors.toMap(this::keyOf, this::valueOf));
    }

    private String keyOf(String qp) {
        if (!qp.contains(QUERY_PARAM_KEY_VALUE_SPLIT)) {
            throw new IllegalArgumentException("유효하지 않은 key value 형식 입니다.");
        }
        String[] split = qp.split(QUERY_PARAM_KEY_VALUE_SPLIT);
        return split[0];
    }

    private String valueOf(String qp) {
        String[] split = qp.split(QUERY_PARAM_KEY_VALUE_SPLIT);
        if (split.length == DONT_HAVE_VALUE) {
            return "";
        }
        return split[1];
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParam() {
        return queryParam;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
