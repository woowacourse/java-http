package org.apache.coyote.http11;

import com.techcourse.exception.client.BadRequestException;

public class HttpRequestStartLine {

    private String method;
    private String uri;
    private String httpVersion;
    private String path;
    private HttpQuery query;

    public HttpRequestStartLine(String method, String uri, String httpVersion, String path, HttpQuery query) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.path = path;
        this.query = query;
    }

    public static HttpRequestStartLine createByString(String raw) {
        String[] split = raw.trim().split(" ");
        String uri = split[1].trim();
        if (split.length != 3) {
            throw new BadRequestException("잘못된 요청 헤더 형식입니다. =" + raw);
        }
        String path = uri;
        HttpQuery httpQuery = null;
        if (uri.contains("?")) {
            path = uri.substring(0, uri.indexOf("?"));
            httpQuery = HttpQuery.createByUri(uri);
        }
        return new HttpRequestStartLine(split[0], uri, split[2], path, httpQuery);
    }

    public String findQuery(String key) {
        return query.findByKey(key);
    }

    public boolean hasQuery() {
        return query != null;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getPath() {
        return path;
    }

    public HttpQuery getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return "HttpRequestStartLine{" +
                "method='" + method + '\'' +
                ", uri='" + uri + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                ", path='" + path + '\'' +
                ", query=" + query +
                '}';
    }
}
