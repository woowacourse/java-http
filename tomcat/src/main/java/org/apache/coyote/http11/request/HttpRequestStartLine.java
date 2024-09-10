package org.apache.coyote.http11.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class HttpRequestStartLine {
    private static final String AVAILABLE_VERSION = "HTTP/1.1";

    private final HttpMethod method;
    private final HttpUrl url;

    public HttpRequestStartLine(String method, String url, String protocolVersion) {
        validateVersion(protocolVersion);
        this.method = HttpMethod.from(method);
        this.url = new HttpUrl(url);
    }

    public HttpRequestStartLine(HttpMethod method, HttpUrl url, String protocolVersion) {
        validateVersion(protocolVersion);
        this.method = method;
        this.url = url;
    }

    private void validateVersion(String version) {
        if (AVAILABLE_VERSION.equals(version)) {
            return;
        }
        throw new UncheckedServletException("해당 버전의 프로토콜을 지원하지 않습니다.");
    }

    public Optional<String> getQueryParameter(String key) {
        return url.getParameter(key);
    }

    public Map<String, String> getQueryParameters() {
        return url.getParameters();
    }

    public String getPath() {
        return url.getPath();
    }

    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        HttpRequestStartLine that = (HttpRequestStartLine) object;
        return method == that.method && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, url);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpRequestStartLine.class.getSimpleName() + "[", "]")
                .add("method=" + method)
                .add("path=" + url)
                .toString();
    }
}
