package nextstep.jwp.handler;

import java.util.Objects;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.QueryParam;

public class HttpRequestMapping {

    private final String url;

    public HttpRequestMapping(final String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public boolean match(final HttpRequest httpRequest) {
        return getUrlExceptQueryParam(httpRequest).equals(url);
    }

    private String getUrlExceptQueryParam(final HttpRequest httpRequest) {
        String url = httpRequest.getUrl();
        if (QueryParam.isQueryParam(url)) {
            return url.split("\\?")[0];
        }
        return httpRequest.getUrl();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpRequestMapping that = (HttpRequestMapping) o;
        return Objects.equals(getUrl(), that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }
}
