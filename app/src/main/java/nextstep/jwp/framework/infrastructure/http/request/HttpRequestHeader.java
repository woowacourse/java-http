package nextstep.jwp.framework.infrastructure.http.request;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import nextstep.jwp.framework.infrastructure.http.header.HttpHeaders;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.protocol.Protocol;

public class HttpRequestHeader {

    private final RequestLine requestLine;
    private final OtherLines otherLines;

    public HttpRequestHeader(String url) {
        this(new RequestLine(HttpMethod.GET, url, Protocol.HTTP1_1, new HashMap<>()),
            new OtherLines(new EnumMap<>(HttpHeaders.class))
        );
    }

    public HttpRequestHeader(RequestLine requestLine, OtherLines otherLines) {
        this.requestLine = requestLine;
        this.otherLines = otherLines;
    }

    public static HttpRequestHeader from(List<String> httpRequestHeaders) {
        if (httpRequestHeaders.isEmpty()) {
            throw new IllegalStateException("Invalid Http Request Header");
        }
        RequestLine requestLine = RequestLine.from(httpRequestHeaders.get(0));
        OtherLines otherLines = OtherLines.from(httpRequestHeaders);
        return new HttpRequestHeader(requestLine, otherLines);
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

    public int getContentLength() {
        return otherLines.getContentLength();
    }
}
