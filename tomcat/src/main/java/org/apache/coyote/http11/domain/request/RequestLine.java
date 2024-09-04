package org.apache.coyote.http11.domain.request;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.domain.HttpMethod;

public class RequestLine {

    HttpMethod method;
    RequestURI requestURI;
    String httpVersion;

    public RequestLine(String line) {
        List<String> tokens = Arrays.stream(line.split(StringUtils.SPACE)).toList();
        validate(tokens);

        this.method = HttpMethod.ofName(tokens.get(0));
        this.requestURI = new RequestURI(tokens.get(1));
        this.httpVersion = tokens.get(2);
    }

    private void validate(List<String> tokens) {
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
