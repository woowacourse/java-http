package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequest {

    private final MappingLine mappingLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(MappingLine mappingLine, Map<String, String> headers, String body) {
        this.mappingLine = mappingLine;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public String toString() {  // TODO 2025. 9. 6. 21:12: header와 body 사이 공백
        return String.join(" ",
                mappingLine.toString(),
                headers.toString(),
                body);
    }
}
