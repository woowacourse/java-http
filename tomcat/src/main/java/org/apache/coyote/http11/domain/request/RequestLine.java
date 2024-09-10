package org.apache.coyote.http11.domain.request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.domain.HttpMethod;

public class RequestLine {

    private final HttpMethod method;
    private final RequestURI requestURI;
    private final String httpVersion;

    public RequestLine(String line) {
        validateLineEmpty(line);
        List<String> tokens = Arrays.stream(line.split(StringUtils.SPACE)).toList();
        validateTokenLength(tokens);

        this.method = HttpMethod.ofName(tokens.get(0));
        this.requestURI = new RequestURI(tokens.get(1));
        this.httpVersion = tokens.get(2);
    }

    private void validateLineEmpty(String line) {
        if (StringUtils.isEmpty(line)) {
            throw new IllegalArgumentException("Line is Empty");
        }
    }

    private void validateTokenLength(List<String> tokens) {
        if (tokens.size() != 3) {
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
