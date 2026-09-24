package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.Headers;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody requestBody;
    private final Cookies cookies;
    private final Map<String, String> parameters;

    public HttpRequest(
            final RequestLine requestLine,
            final Headers headers,
            final RequestBody requestBody
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
        this.cookies = new Cookies(headers.cookie());
        this.parameters = parseParameters(requestBody.getValue());
    }

    public String getSessionId() {
        return cookies.getSessionId();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    private static Map<String, String> parseParameters(final String requestBody) {
        if (requestBody.isBlank()) {
            return Map.of();
        }

        final Map<String, String> parameters = new HashMap<>();
        for (String pair : requestBody.split("&")) {
            final String[] nameAndValue = pair.split("=", 2);
            if (nameAndValue.length == 2) {
                parameters.put(
                        decode(nameAndValue[0].trim()),
                        decode(nameAndValue[1].trim())
                );
            }
        }
        return Map.copyOf(parameters);
    }

    private static String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

}
