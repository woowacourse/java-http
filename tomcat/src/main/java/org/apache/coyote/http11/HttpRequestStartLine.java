package org.apache.coyote.http11;

import com.techcourse.exception.client.BadRequestException;
import org.apache.coyote.http11.query.HttpQuery;

public class HttpRequestStartLine {

    private HttpMethod method;
    private String uri;
    private String httpVersion;
    private String path;
    private HttpQuery httpQuery;

    public HttpRequestStartLine(HttpMethod method, String uri, String httpVersion, String path, HttpQuery httpQuery) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.path = path;
        this.httpQuery = httpQuery;
    }

    public static HttpRequestStartLine create(String raw) {
        String[] split = raw.trim().split(" ");
        HttpMethod method = HttpMethod.findByName(split[0]);
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
        return new HttpRequestStartLine(method, uri, split[2], path, httpQuery);
    }

    public String findQuery(String key) {
        return httpQuery.findByKey(key);
    }

    public boolean hasQuery() {
        return httpQuery != null;
    }

    public boolean isSameMethod(HttpMethod method) {
        return this.method == method;
    }

    public boolean isGet() {
        return method.isGet();
    }

    public boolean isPost() {
        return method.isPost();
    }

    public HttpMethod getMethod() {
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

    public HttpQuery getHttpQuery() {
        return httpQuery;
    }

    @Override
    public String toString() {
        return "HttpRequestStartLine{" +
                "method='" + method + '\'' +
                ", uri='" + uri + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                ", path='" + path + '\'' +
                ", query=" + httpQuery +
                '}';
    }
}
