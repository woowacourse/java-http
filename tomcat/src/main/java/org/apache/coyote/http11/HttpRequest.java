package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final byte[] body;
    private final FormParameters formParameters;

    HttpRequest(
            RequestLine requestLine,
            Map<String, String> headers,
            byte[] body,
            FormParameters formParameters
    ) {
        this.requestLine = requestLine;
        this.headers = Map.copyOf(headers);
        this.body = body.clone();
        this.formParameters = formParameters;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase(Locale.ROOT));
    }

    public String getBody() {
        return new String(body, StandardCharsets.UTF_8);
    }

    public String getParameter(String name) {
        return formParameters.get(name);
    }
}
