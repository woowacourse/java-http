package org.apache.coyote.http11;

public class HttpRequest {
    private static final int START_LINE_INDEX = 0;

    private HttpRequestStartLine startLine;
    private HttpHeaders httpHeaders;
    private String body;

    public HttpRequest(HttpRequestStartLine startLine, HttpHeaders httpHeaders,
                       String body) {
        this.startLine = startLine;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public static HttpRequest createByString(String raw) {
        String[] lines = raw.split("\r\n");
        HttpRequestStartLine startLine = HttpRequestStartLine.createByString(lines[START_LINE_INDEX]);
        HttpHeaders headers = new HttpHeaders();

        int i = 1;
        while (i < lines.length && !lines[i].isEmpty()) {
            headers.addByString(lines[i]);
            i++;
        }
        // TODO Body 처리
        return new HttpRequest(startLine, headers, null);
    }

    public String findQuery(String key) {
        return startLine.findQuery(key);
    }

    public String getBody() {
        return body;
    }

    public String getUri() {
        return startLine.getUri();
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
