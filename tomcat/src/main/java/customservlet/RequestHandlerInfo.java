package customservlet;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.util.HttpMethod;

public class RequestHandlerInfo {

    private final String url;
    private final HttpMethod httpMethod;

    public RequestHandlerInfo(final String url, final HttpMethod httpMethod) {
        this.url = url;
        this.httpMethod = httpMethod;
    }

    public static RequestHandlerInfo of(final String url, final HttpMethod httpMethod) {
        return new RequestHandlerInfo(url, httpMethod);
    }

    public boolean canSupport(final HttpRequest request) {
        return request.getRequestUri().containUrl(url) && request.getHttpMethod() == httpMethod;
    }
}
