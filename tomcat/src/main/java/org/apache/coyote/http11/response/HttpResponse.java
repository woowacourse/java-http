package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.response.body.ResponseBody;
import org.apache.coyote.http11.response.headers.ResponseHeaders;
import org.apache.coyote.http11.response.statusLine.StatusLine;

import java.util.Objects;

public class HttpResponse {

    private static final String NEW_LINE = "\r\n";

    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;

    private HttpResponse(final StatusLine statusLine, final ResponseHeaders responseHeaders, final ResponseBody responseBody) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final HttpVersion httpVersion, final ResponseEntity responseEntity) {
        return new HttpResponse(
                StatusLine.of(httpVersion, responseEntity.getHttpStatus()),
                ResponseHeaders.from(responseEntity),
                ResponseBody.from(responseEntity.getContent())
        );
    }

    public String convertToString() {
        return String.join(NEW_LINE,
                statusLine.convertToString(),
                responseHeaders.convertToString(),
                "",
                responseBody.getContent());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final HttpResponse that = (HttpResponse) o;
        return Objects.equals(statusLine, that.statusLine) && Objects.equals(responseHeaders, that.responseHeaders) && Objects.equals(responseBody, that.responseBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusLine, responseHeaders, responseBody);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusLine=" + statusLine +
                ", responseHeaders=" + responseHeaders +
                ", responseBody=" + responseBody +
                '}';
    }
}
