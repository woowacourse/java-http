package org.apache.coyote.http11.request;


import com.techcourse.exception.UncheckedServletException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;

public class Http11RequestLine {

    private static final int REQUEST_LINE_LENGTH = 3;
    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String REQUEST_LINE_SEPARATOR = " ";
    public static final String VERSION_OF_PROTOCOL = "HTTP/1.1";

    private final HttpMethod method;
    private final String uri;

    public Http11RequestLine(String line) {
        String[] seperatedLine = line.split(REQUEST_LINE_SEPARATOR);
        validate(seperatedLine);
        this.method = HttpMethod.from(seperatedLine[0]);
        this.uri = seperatedLine[1];
        validateProtocol(seperatedLine[2]);
    }

    private void validate(String[] seperatedLine) {
        if (seperatedLine.length != REQUEST_LINE_LENGTH) {
            throw new UncheckedServletException(new IllegalArgumentException("유효한 HTTP RequestLine이 아닙니다."));
        }
    }

    private void validateProtocol(String versionOfProtocol) {
        if (!VERSION_OF_PROTOCOL.equals(versionOfProtocol)) {
            throw new UncheckedServletException(new IllegalArgumentException("유효한 HTTP 프로토콜 형식이 아닙니다."));
        }
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public String getURI() {
        return this.uri;
    }

    public String getPath() {
        String uri = getURI();
        if (existsQueryString()) {
            int index = uri.indexOf("?");
            return uri.substring(0, index);
        }
        return uri;
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

    private boolean existsQueryString() {
        return getURI().contains(QUERY_STRING_DELIMITER);
    }
}
