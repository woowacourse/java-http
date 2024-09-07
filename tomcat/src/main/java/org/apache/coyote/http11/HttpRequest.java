package org.apache.coyote.http11;

import org.apache.coyote.http11.query.Query;

public class HttpRequest {
    private static final int START_LINE_INDEX = 0;

    private HttpRequestStartLine startLine;
    private HttpHeaders httpHeaders;
    private Query body;

    public HttpRequest(HttpRequestStartLine startLine, HttpHeaders httpHeaders,
                       Query body) {
        this.startLine = startLine;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public static HttpRequest createByString(String raw) {
        String[] lines = raw.split("\r\n");
        HttpRequestStartLine startLine = HttpRequestStartLine.createByString(lines[START_LINE_INDEX]);
        HttpHeaders headers = new HttpHeaders();
        Query body = null;
        int i = 1;
        while (i < lines.length && !lines[i].isEmpty()) {
            headers.addByString(lines[i]);
            i++;
        }
        if (i + 1 < lines.length) {
            body = Query.create(lines[i + 1]);
        }

        return new HttpRequest(startLine, headers, body);
    }

    public boolean isSameMethod(HttpMethod method) {
        return startLine.isSameMethod(method);
    }

    public String findFromQueryParam(String key) {
        return startLine.findQuery(key);
    }

    public String findFromBody(String key) {
        return body.findByKey(key);
    }

    public boolean hasQuery() {
        return startLine.hasQuery();
    }


    public String getPath() {
        return startLine.getPath();
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "startLine=" + startLine +
                ", httpHeaders=" + httpHeaders +
                ", body='" + body + '\'' +
                '}';
    }
}
