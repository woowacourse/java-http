package org.apache.coyote.http11.parser.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.dto.request.HttpRequest;
import org.apache.coyote.http11.dto.request.RequestBody;
import org.apache.coyote.http11.dto.request.RequestHeader;
import org.apache.coyote.http11.dto.request.RequestLine;

public final class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parse(final BufferedReader reader) throws IOException {
        // 1. 요청 라인(Request Line) 파싱
        final RequestLine requestLine = RequestLineParser.parse(reader);

        // 2. 헤더 파싱
        final RequestHeader requestHeader = RequestHeaderParser.parse(reader);

        // 3. 바디 파싱
        final RequestBody requestBody = RequestBodyParser.parse(reader, requestHeader.getContentLength());

        return new HttpRequest(requestLine, requestHeader, requestBody);
    }
}
