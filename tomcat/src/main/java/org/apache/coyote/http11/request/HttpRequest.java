package org.apache.coyote.http11.request;

import static org.apache.coyote.QueryStringParser.hasQueryString;
import static org.apache.coyote.QueryStringParser.parseQueryString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final Map<String, String> body;

    private HttpRequest(final RequestLine requestLine, final HttpHeaders headers,
                        final Map<String, String> body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final String firstLine, final List<String> values) {
        final RequestLine requestLine = RequestLine.of(firstLine);

        final int index = values.indexOf("");

        final Map<String, String> headers = new HashMap<>();
        final List<String> headerValues = values.subList(0, index);

        for (String value : headerValues) {
            final String[] header = value.split(": ");
            headers.put(header[0], header[1]);
        }

        final List<String> bodyValues = values.subList(index + 1, values.size());
        final Map<String, String> body = new HashMap<>();
        for (String bodyValue : bodyValues) {
            if (hasQueryString(bodyValue)) {
                body.putAll(parseQueryString(bodyValue));
                continue;
            }
            final String[] value = bodyValue.split(": ");
            body.put(value[0], value[1]);
        }

        final HttpHeaders httpHeaders = new HttpHeaders(headers);
        return new HttpRequest(requestLine, httpHeaders, body);
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public String getUrl() {
        return requestLine.getUrl();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getHeaders() {
        return headers.getValues();
    }

    public Map<String, String> getBody() {
        return body;
    }
}
