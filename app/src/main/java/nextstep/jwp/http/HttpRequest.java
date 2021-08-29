package nextstep.jwp.http;

public class HttpRequest {

    private HttpMethod httpMethod;

    private URI uri;

    private Protocol protocol;

    private HttpHeader httpHeader;

    public HttpRequest(HttpMethod httpMethod, URI uri, Protocol protocol, HttpHeader httpHeader) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.protocol = protocol;
        this.httpHeader = httpHeader;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return this.uri.getPath();
    }

    public QueryStrings getQueryStrings() {
        return this.uri.getQueryStrings();
    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    public HttpHeader getHttpHeader() {
        return this.httpHeader;
    }

    public boolean hasQueryStrings() {
        return !getQueryStrings().isEmpty();
    }
}
