package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.start.HttpVersion;

import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {
    public static final String HEADER_END_POINT = " \r\n";
    public static final String END_POINT = "\r\n";
    private String startLine;
    private String headers;
    private String body;

    private HttpResponse(final String startLine, final String headers, final String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse init() {
        return new HttpResponse(null, null, "");
    }

    private static String makeHeader(final Map<String, String> header) {
        return header.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + HEADER_END_POINT)
                .collect(Collectors.joining());
    }

    public void setHeader(final HttpVersion httpVersion, final HttpStatus httpStatus, final Map<String, String> header) {
        this.startLine = httpVersion.getVersion() + " " + httpStatus.getStatusName() + HEADER_END_POINT;
        this.headers = makeHeader(header);
    }

    public void setHttpStatus(final HttpVersion httpVersion, final HttpStatus httpStatus) {
        this.startLine = httpVersion.getVersion() + " " + httpStatus.getStatusName() + END_POINT;
        this.headers = "";
    }

    public void setBody(final String responseBody) {
        this.headers += "Content-Length: " + responseBody.getBytes().length + HEADER_END_POINT;
        this.body = END_POINT + responseBody;
    }

    public void setRedirect(final String redirectUri) {
        this.headers += "Location: " + redirectUri + HEADER_END_POINT;
    }

    public byte[] getBytes() {
        return (startLine + headers + body).getBytes();
    }
}
