package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.HttpMethod;

public class RequestLine {

    HttpMethod method;
    String requestURI;
    String HttpVersion;

    public RequestLine(String line) {
        List<String> tokens = Arrays.stream(line.split(StringUtils.SPACE)).toList();
        validate(tokens);

        this.method = HttpMethod.ofName(tokens.get(0));
        this.requestURI = tokens.get(1);
        this.HttpVersion = tokens.get(2);
    }

    private void validate(List<String> tokens) {
        if (tokens.size() != 3) {
            throw new IllegalArgumentException("Invalid Request Line Length: " + tokens.size());
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getHttpVersion() {
        return HttpVersion;
    }
}
