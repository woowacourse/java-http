package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;

public record RequestLine(
        HttpMethod method,
        HttpURL url,
        String version
) {
    public static RequestLine from(String requestLine) {
        String[] tokens = requestLine.split(" ");
        HttpMethod method = HttpMethod.from(tokens[0]);
        HttpURL url = HttpURL.from(tokens[1]);
        String version = tokens[2];
        return new RequestLine(method, url, version);
    }

    public String path() {
        return url().path();
    }

    public Map<String, String> getQueryParameters() {
        return Collections.unmodifiableMap(url().queryParameters());
    }
}
