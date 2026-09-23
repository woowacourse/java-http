package org.apache.coyote.http11.request;

import java.util.Objects;

public class RequestLine {

    private final HttpMethod method;
    private final RequestUri uri;
    private final String protocolVersion;

    public RequestLine(final String requestLine) {
        Objects.requireNonNull(requestLine, "요청의 첫 줄은 비어있을 수 없습니다.");

        final String[] components = requestLine.strip().split(" ");
        if (components.length != 3) {
            throw new IllegalArgumentException("잘못된 RequestLine: " + requestLine);
        }

        this.method = HttpMethod.from(components[0]);
        this.uri = new RequestUri(components[1]);
        this.protocolVersion = components[2];

    }

    public boolean isPost() {
        return HttpMethod.POST.equals(method);
    }

    public boolean isGet() {
        return HttpMethod.GET.equals(method);
    }

    public String getPath() {
        return uri.getPath();
    }

}
