package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;

public class HttpRequestFirstLine {

    private final HttpMethod httpMethod;
    private final HttpPath httpPath;
    private final String protocolVersion;

    public static HttpRequestFirstLine from(final String firstLine) {
        final List<String> firstLines = Arrays.asList(firstLine.split(" "));
        return new HttpRequestFirstLine(firstLines.get(0), firstLines.get(1), firstLines.get(2));
    }

    public HttpRequestFirstLine(
            final String httpMethod,
            final String uri,
            final String protocolVersion
    ) {
        this.httpMethod = HttpMethod.valueOf(httpMethod);
        this.httpPath = HttpPath.from(uri);
        this.protocolVersion = protocolVersion;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpPath getHttpPath() {
        return httpPath;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
