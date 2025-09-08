package org.apache.coyote.http11.httpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.httpRequest.HttpRequest;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private final StatusLine statusLine;
    private final ResponseHeader headers;
    private final ResponseBody responseBody;

    private HttpResponse(
            final StatusLine statusLine,
            final ResponseHeader headers,
            final ResponseBody responseBody
    ) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse build(final HttpRequest httpRequest) throws IOException {
        final ResponseBody responseBody = new ResponseBody(httpRequest);
        final ResponseContent responseContent = responseBody.getContent();

        final ResponseHeader responseHeader = ResponseHeader.build(httpRequest, responseContent);

        final HttpStatus httpStatus = responseContent.httpStatus();
        final StatusLine statusLine = StatusLine.build(httpStatus);

        return new HttpResponse(statusLine, responseHeader, responseBody);
    }

    public byte[] getBytes() throws IOException {
        final ResponseContent content = responseBody.getContent();

        final String header = buildHeader();
        final String body = content.body();

        return (header + body).getBytes(StandardCharsets.UTF_8);
    }

    private String buildHeader() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(statusLine.toString()).append(CRLF);

        for (String header : headers.getHeaders()) {
            System.out.println(header);
            stringBuilder.append(header).append(CRLF);
        }
        stringBuilder.append(CRLF);

        return stringBuilder.toString();
    }
}
