package org.apache.coyote.http11;


import com.techcourse.exception.UncheckedServletException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class Http11RequestLine {

    private static final List<String> METHODS =
            List.of("POST", "GET", "HEAD", "PUT", "PATCH", "DELETE", "CONNECT", "TRACE", "OPTIONS");
    private static final String PROTOCOL = "HTTP";
    private static final String PROTOCOL_VERSION = "1.1";
    private static final int REQUEST_LINE_LENGTH = 3;
    private static final int VERSION_OF_PROTOCOL_LENGTH = 2;
    private static final String VERSION_OF_PROTOCOL_DELIMITER = "/";
    private static final String QUERY_STRING_DELIMITER = "?";

    private final Map<String, String> line;


    public Http11RequestLine(String line) {
        validate(line);
        this.line = new HashMap<>();
        this.line.put("Method", line.split(" ")[0]);
        this.line.put("URI", line.split(" ")[1]);
        this.line.put("Protocol", line.split(" ")[2]);
    }

    private void validate(String line) {
        if (StringUtils.isBlank(line) || line.split(" ").length != REQUEST_LINE_LENGTH) {
            throw new UncheckedServletException(new IllegalArgumentException("유효한 HTTP RequestLine이 아닙니다."));
        }
        validateMethod(line);
        validateProtocol(line);
    }

    private void validateMethod(String startLine) {
        String method = startLine.split(" ")[0];
        if (!METHODS.contains(method)) {
            throw new UncheckedServletException(new IllegalArgumentException("유효한 HTTP Method가 아닙니다."));
        }
    }

    private void validateProtocol(String startLine) {
        String protocol = startLine.split(" ")[2];
        if (protocol.split(VERSION_OF_PROTOCOL_DELIMITER).length != VERSION_OF_PROTOCOL_LENGTH) {
            throw new UncheckedServletException(new IllegalArgumentException("유효한 HTTP 프로토콜 형식이 아닙니다."));
        }
        if (!PROTOCOL.equals(protocol.split(VERSION_OF_PROTOCOL_DELIMITER)[0])) {
            throw new UncheckedServletException(new IllegalArgumentException("요청이 HTTP 프로토콜이 아닙니다."));
        }
        if (!PROTOCOL_VERSION.equals(protocol.split(VERSION_OF_PROTOCOL_DELIMITER)[1])) {
            throw new UncheckedServletException(new IllegalArgumentException("유효한 HTTP 프로토콜 버전이 아닙니다."));
        }
    }

    public String getVersionOfProtocol() {
        return line.get("Protocol");
    }

    public String getMethod() {
        return line.get("Method");
    }

    public String getURI() {
        return line.get("URI");
    }

    public String getPath() {
        String uri = getURI();
        if (existsQueryString()) {
            int index = uri.indexOf("?");
            return uri.substring(0, index);
        }
        return uri;
    }

    public boolean existsQueryString() {
        return getURI().contains(QUERY_STRING_DELIMITER);
    }

    public Map<String, String> getQueryParam() {
        if (!existsQueryString()) {
            throw new UncheckedServletException(new UnsupportedOperationException("QueryString 이 존재하지 않는 요청입니다."));
        }
        Map<String, String> queryParam = new HashMap<>();
        int index = getURI().indexOf(QUERY_STRING_DELIMITER);
        String queryString = getURI().substring(index + 1);
        for (String query : queryString.split("&")) {
            String key = query.split("=")[0];
            String value = query.split("=")[1];
            queryParam.put(key, value);
        }
        return queryParam;
    }
}
