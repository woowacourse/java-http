package nextstep.jwp.model.httpmessage.request;

import nextstep.jwp.util.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private final HttpMethod method;
    private final String path;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine) {
        log.debug("Request line : {}", requestLine);
        String[] tokens = requestLine.split(" ");
        if (tokens.length != 3) {
            throw new IllegalArgumentException("request Line 형식이 올바르지 않습니다.");
        }

        method = HttpMethod.valueOf(tokens[0]);
        if (method.isPost()) {
            path = tokens[1];
            return;
        }

        int index = tokens[1].indexOf("?");
        if (index == -1) {
            path = tokens[1];
            return;
        }

        path = tokens[1].substring(0, index);
        params = HttpRequestUtils.parseQueryString(tokens[1].substring(index + 1));
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
