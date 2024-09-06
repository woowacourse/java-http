package http;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpResponseHeaders {

    private final List<HttpHeader> headers;

    public HttpResponseHeaders(String contentMediaType, int contentLength, List<HttpHeader> headers) {
        this.headers = new ArrayList<>();
        this.headers.add(new HttpHeader("Content-Type", contentMediaType + ";charset=utf-8"));
        this.headers.add(new HttpHeader("Content-Length", String.valueOf(contentLength)));
        this.headers.addAll(headers);
    }

    public String buildHeaders() {
        return headers.stream()
                .map(HttpHeader::buildHeader)
                .collect(Collectors.joining("\r\n"));
    }
}
