package nextstep.jwp.http.request;

import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpHeader;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.http.Protocol;

public class HttpRequest {

    private final HttpMethod httpMethod;

    private final URI uri;

    private final Protocol protocol;

    private final HttpHeader httpHeader;

    private final RequestBody requestBody;

    public HttpRequest(HttpMethod httpMethod, URI uri, Protocol protocol, HttpHeader httpHeader, RequestBody requestBody) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.protocol = protocol;
        this.httpHeader = httpHeader;
        this.requestBody = requestBody;
    }

    public boolean hasQueryStrings() {
        return !getQueryStrings().isEmpty();
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

    public RequestBody getRequestBody() {
        return this.requestBody;
    }

    public HttpSession getSession() {
        HttpCookie httpCookie = HttpCookie.fromHeader(httpHeader);
        return HttpSessions.getSession(httpCookie.getSessionId());
    }
}
