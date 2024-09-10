package org.apache.coyote.http11.request;


import com.techcourse.exception.UncheckedServletException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;

public class Http11RequestLine {

    private static final int REQUEST_LINE_LENGTH = 3;
    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String REQUEST_LINE_SEPARATOR = " ";
    private static final String VERSION_OF_PROTOCOL = "HTTP/1.1";
    private static final int METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int VERSION_OF_PROTOCOL_INDEX = 2;
    private static final String QUERY_STRINGS_DELIMITER = "&";
    private static final String QUERY_STRING_KEY_VALUE_DELIMITER = "=";
    private static final int QUERY_STRING_KEY_INDEX = 0;
    private static final int QUERY_STRING_VALUE_INDEX = 1;

    private final HttpMethod method;
    private final String uri;

    public Http11RequestLine(String line) {
        String[] seperatedLine = line.split(REQUEST_LINE_SEPARATOR);
        validate(seperatedLine);
        this.method = HttpMethod.from(seperatedLine[METHOD_INDEX]);
        this.uri = seperatedLine[REQUEST_URI_INDEX];
        validateProtocol(seperatedLine[VERSION_OF_PROTOCOL_INDEX]);
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
            int index = uri.indexOf(QUERY_STRING_DELIMITER);
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
        for (String query : queryString.split(QUERY_STRINGS_DELIMITER)) {
            String key = query.split(QUERY_STRING_KEY_VALUE_DELIMITER)[QUERY_STRING_KEY_INDEX];
            String value = query.split(QUERY_STRING_KEY_VALUE_DELIMITER)[QUERY_STRING_VALUE_INDEX];
            queryParam.put(key, value);
        }
        return queryParam;
    }

    private boolean existsQueryString() {
        return getURI().contains(QUERY_STRING_DELIMITER);
    }
}
