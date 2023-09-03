package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;

public class HttpRequestFirstLine {

    private final String method;
    private final HttpPath httpPath;
    private final String protocolVersion;

    public static HttpRequestFirstLine from(final String firstLine) {
        final List<String> firstLines = Arrays.asList(firstLine.split(" "));
        return new HttpRequestFirstLine(firstLines.get(0), firstLines.get(1), firstLines.get(2));
    }

    public HttpRequestFirstLine(
            final String method,
            final String uri,
            final String protocolVersion
    ) {
        this.method = method;
        this.httpPath = HttpPath.from(uri);
        this.protocolVersion = protocolVersion;
    }

    public String getMethod() {
        return method;
    }

    public HttpPath getHttpPath() {
        return httpPath;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

}
