package nextstep.jwp.framework.http;

import java.util.Map;

public class HttpBody {

    private final QueryParams body;

    public HttpBody() {
        this.body = new QueryParams();
    }

    public HttpBody(final String body) {
        this.body = new QueryParams(body);
    }

    public HttpBody(final QueryParams body) {
        this.body = body;
    }

    public boolean hasBody() {
        return !(body.count() == 0);
    }

    public boolean hasNotBody() {
        return body.count() == 0;
    }

    public Map<String, String> getBody() {
        return body.getQueryParams();
    }

    @Override
    public String toString() {
        return body.toString();
    }
}
