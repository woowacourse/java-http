package nextstep.jwp.web.http.request;

import java.util.Objects;

public class MethodUrl {

    private final HttpMethod method;
    private final String url;
    private final QueryParams queryParams;

    public MethodUrl(HttpMethod method, String url) {
        this(method, url, QueryParams.empty());
    }

    public MethodUrl(HttpMethod method, String url,
                     QueryParams queryParams) {
        this.method = method;
        this.url = url;
        this.queryParams = queryParams;
    }

    public String url() {
        return url;
    }

    public HttpMethod method() {
        return method;
    }

    public QueryParams queryParams() {
        return queryParams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MethodUrl methodUrl = (MethodUrl) o;
        return method == methodUrl.method && Objects.equals(url, methodUrl.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, url);
    }

    @Override
    public String toString() {
        return method + " " + url;
    }
}
