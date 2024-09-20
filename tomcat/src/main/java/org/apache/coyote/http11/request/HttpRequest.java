package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http11.headers.HttpCookies;
import org.apache.coyote.http11.headers.HttpHeaders;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final RequestBody body;

    public HttpRequest(String rawRequestLine, HttpHeaders headers, RequestBody body) {
        this.requestLine = new RequestLine(rawRequestLine);
        this.headers = headers;
        this.body = body;
    }

    public HttpRequestMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getPathWithoutQueryString() {
        return getPath().split("\\?")[0];
    }

    public Map<String, String> getQueryString() {
        if (getPath().contains("?")) {
            String queryString = getPath().substring(getPathWithoutQueryString().length());

            return Arrays.stream(queryString.split("&"))
                    .map(query -> query.split("=", 2))
                    .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
        }
        return Map.of();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public HttpCookies getCookies() {
        return headers.getCookies();
    }

    public RequestBody getBody() {
        return body;
    }

    public String render() {
        return requestLine.render() + " \r\n" + headers.render() + "\r\n\r\n" + body;
    }

    @Override
    public String toString() {
        return render();
    }
}
