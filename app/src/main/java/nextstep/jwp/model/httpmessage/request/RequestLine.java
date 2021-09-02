package nextstep.jwp.model.httpmessage.request;

import nextstep.jwp.util.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);
    public static final int REQUEST_SPLIT_COUNT = 3;
    public static final int PATH_INDEX = 1;
    public static final String QUERY_PARAM_DELIMITER = "?";

    private final HttpMethod method;
    private final String path;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine) {
        log.debug("Request line : {}", requestLine);
        String[] tokens = requestLine.split(" ");
        if (tokens.length != REQUEST_SPLIT_COUNT) {
            throw new IllegalArgumentException("request Line 형식이 올바르지 않습니다.");
        }

        method = HttpMethod.valueOf(tokens[0]);
        if (method.isPost()) {
            path = tokens[PATH_INDEX];
            return;
        }

        int index = tokens[PATH_INDEX].indexOf(QUERY_PARAM_DELIMITER);
        if (index == -1) {
            path = tokens[PATH_INDEX];
            return;
        }

        path = tokens[PATH_INDEX].substring(0, index);
        params = HttpRequestUtils.parseQueryString(tokens[PATH_INDEX].substring(index + 1));
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getParameter(String param) {
        return params.computeIfAbsent(param, key -> something(param));
    }

    private String something(String param) {
        throw new IllegalArgumentException("해당 쿼리 파라미터가 존재하지 않습니다. (입력 : " + param + ")");
    }
}
