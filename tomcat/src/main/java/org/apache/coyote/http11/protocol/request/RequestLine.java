package org.apache.coyote.http11.protocol.request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class RequestLine {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int VALID_REQUEST_LINE_LENGTH = 3;

    private final HttpMethod method;
    private final RequestURI requestURI;
    private final String httpVersion;

    public RequestLine(String line) {
        validateLineEmpty(line);
        List<String> tokens = Arrays.stream(line.split(StringUtils.SPACE)).toList();
        validateTokenLength(tokens);

        this.method = HttpMethod.ofName(tokens.get(HTTP_METHOD_INDEX));
        this.requestURI = new RequestURI(tokens.get(REQUEST_URI_INDEX));
        this.httpVersion = tokens.get(HTTP_VERSION_INDEX);
    }

    private void validateLineEmpty(String line) {
        if (StringUtils.isEmpty(line)) {
            throw new IllegalArgumentException("Line is Empty");
        }
    }

    private void validateTokenLength(List<String> tokens) {
        if (tokens.size() != VALID_REQUEST_LINE_LENGTH) {
            throw new IllegalArgumentException("Invalid Request Line Length: " + tokens.size());
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return requestURI.getPath();
    }

    public String getQueryParameter(String key) {
        return requestURI.getQueryParameter(key);
    }

    public Map<String, String> getQueryParameters() {
        return requestURI.getQueryParameters();
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
