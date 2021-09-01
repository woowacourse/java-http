package nextstep.jwp.framework.infrastructure.http.request;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import nextstep.jwp.framework.infrastructure.http.cookie.HttpCookies;
import nextstep.jwp.framework.infrastructure.http.header.HttpHeaders;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.protocol.Protocol;

public class HttpRequestHeader {

    private final RequestLine requestLine;
    private final OtherRequestLines otherRequestLines;
    private final HttpCookies httpCookies;

    public HttpRequestHeader(String url) {
        this(new RequestLine(HttpMethod.GET, url, Protocol.HTTP1_1, new HashMap<>()),
            new OtherRequestLines(new EnumMap<>(HttpHeaders.class)),
            new HttpCookies(new HashMap<>())
        );
    }

    public HttpRequestHeader(
        RequestLine requestLine,
        OtherRequestLines otherRequestLines,
        HttpCookies httpCookies
    ) {
        this.requestLine = requestLine;
        this.otherRequestLines = otherRequestLines;
        this.httpCookies = httpCookies;
    }

    public static HttpRequestHeader from(List<String> httpRequestHeaders) {
        if (httpRequestHeaders.isEmpty()) {
            throw new IllegalStateException("Invalid Http Request Header");
        }
        RequestLine requestLine = RequestLine.from(httpRequestHeaders.get(0));
        OtherRequestLines otherRequestLines = OtherRequestLines.from(httpRequestHeaders);
        HttpCookies httpCookies = HttpCookies.from(httpRequestHeaders);
        return new HttpRequestHeader(requestLine, otherRequestLines, httpCookies);
    }

    public HttpMethod getMethod() {
        return requestLine.getHttpMethod();
    }

    public String getUrl() {
        return requestLine.getUrl();
    }

    public Protocol getProtocol() {
        return requestLine.getProtocol();
    }

    public String getContentLength() {
        return otherRequestLines.get(HttpHeaders.CONTENT_LENGTH);
    }

    public HttpCookies getCookie() {
        return httpCookies;
    }
}
