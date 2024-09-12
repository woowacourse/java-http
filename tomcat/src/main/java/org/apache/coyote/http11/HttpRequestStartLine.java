package org.apache.coyote.http11;

import com.techcourse.exception.client.BadRequestException;
import org.apache.coyote.http11.query.HttpQuery;

public class HttpRequestStartLine {

    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int START_LINE_LENGTH = 3;
    private static final String DELIMITER = " ";

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
        String[] split = raw.trim().split(DELIMITER);
        HttpMethod method = HttpMethod.findByName(split[METHOD_INDEX]);
        String uri = split[URI_INDEX].trim();
        if (split.length != START_LINE_LENGTH) {
            throw new BadRequestException("잘못된 요청 헤더 형식입니다. =" + raw);
        }
        String path = HttpQuery.extractPath(uri);
        HttpQuery httpQuery = HttpQuery.createByUri(uri);

        return new HttpRequestStartLine(method, uri, split[HTTP_VERSION_INDEX], path, httpQuery);
    }

    public String findQuery(String key) {
        return httpQuery.findByKey(key);
    }

    public boolean hasNoQuery() {
        return httpQuery == null;
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
