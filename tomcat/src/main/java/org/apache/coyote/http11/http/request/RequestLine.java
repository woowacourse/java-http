package org.apache.coyote.http11.http.request;



import org.apache.coyote.http11.http.HttpMethod;
import org.apache.coyote.http11.http.HttpVersion;

public class RequestLine {

    private final HttpMethod method;
    private final String path;
    private final HttpVersion version;
    private final String queryString;

    public RequestLine(String requestLine) {
        String[] parts = requestLine.split(" ", 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException("잘못된 요청입니다: " + requestLine);
        }

        this.method = HttpMethod.from(parts[0]);
        this.version = HttpVersion.from(parts[2]);

        int queryIndex = parts[1].indexOf("?");
        if (queryIndex >= 0) {
            this.path = parts[1].substring(0, queryIndex);
            this.queryString = parts[1].substring(queryIndex + 1);
        } else {
            this.path = parts[1];
            this.queryString = "";
        }
    }

    public boolean isMethod(HttpMethod target) {
        return this.method == target;
    }

    public HttpMethod method() { return method; }
    public String path() { return path; }
    public HttpVersion version() { return version; }
    public String queryString() { return queryString; }
}
