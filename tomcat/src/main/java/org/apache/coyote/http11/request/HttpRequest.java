package org.apache.coyote.http11.request;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;
    private final Map<String, String> parameters;

    public HttpRequest(final InputStream inputStream) throws IOException {
        this.requestLine = new RequestLine(inputStream);
        this.headers = new RequestHeaders(inputStream);
        this.body = new RequestBody(inputStream, headers);
        this.parameters = parseParameters(requestLine.getTarget(), body.getContent());
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getHeaders() {
        return headers;
    }

    public RequestBody getBody() {
        return body;
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public boolean hasParameters() {
        return !parameters.isEmpty();
    }

    private Map<String, String> parseParameters(final String target, final String body) {
        final Map<String, String> parameters = new LinkedHashMap<>();
        parseParameterString(getQueryString(target), parameters);
        parseParameterString(body, parameters);
        return parameters;
    }

    private String getQueryString(final String target) {
        final int queryStart = target.indexOf('?');
        if (queryStart == -1) {
            return null;
        }
        return target.substring(queryStart + 1);
    }

    private void parseParameterString(final String parameterString, final Map<String, String> parameters) {
        if (parameterString == null || parameterString.isEmpty()) {
            return;
        }

        for (String parameter : parameterString.split("&")) {
            final int separator = parameter.indexOf('=');
            final String encodedName = separator == -1 ? parameter : parameter.substring(0, separator);
            final String encodedValue = separator == -1 ? "" : parameter.substring(separator + 1);
            parameters.putIfAbsent(decode(encodedName), decode(encodedValue));
        }
    }

    private String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
