package org.springframework;

import java.util.Objects;
import org.apache.http.HttpRequest;
import org.apache.http.info.HttpMethod;
import org.springframework.annotation.RequestMapping;

public class RequestMappingInfo {

    public static final RequestMappingInfo EMPTY = new RequestMappingInfo(null, null);

    private final HttpMethod httpMethod;
    private final String uri;

    public RequestMappingInfo(final HttpMethod httpMethod, final String uri) {
        this.httpMethod = httpMethod;
        this.uri = uri;
    }

    public static RequestMappingInfo from(final RequestMapping requestMapping) {
        return new RequestMappingInfo(requestMapping.method(), requestMapping.uri());
    }

    public static RequestMappingInfo from(final HttpRequest httpRequest) {
        return new RequestMappingInfo(httpRequest.getHttpMethod(), httpRequest.getRequestURIWithoutQueryParams());
    }

    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    public String getUri() {
        return this.uri;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestMappingInfo that = (RequestMappingInfo) o;
        return httpMethod == that.httpMethod && Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, uri);
    }
}
