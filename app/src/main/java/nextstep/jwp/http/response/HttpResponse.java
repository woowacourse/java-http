package nextstep.jwp.http.response;

import java.util.List;
import nextstep.jwp.http.common.Body;
import nextstep.jwp.http.common.HttpVersion;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final Body body;

    public HttpResponse(StatusLine statusLine,
        ResponseHeaders responseHeaders, Body body) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.body = body;
    }

    public static HttpResponse status(HttpStatus httpStatus, Body body) {
        final ResponseHeaders headers = new ResponseHeaders();
        headers.putHeader("Content-Length", String.valueOf(body.length()));
        headers.putHeader("Content-Type", ContentType.TEXT.getValue());
        return of(httpStatus, headers, body);
    }

    private static HttpResponse of(HttpStatus httpStatus, ResponseHeaders headers, Body body) {
        StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, httpStatus);
        return new HttpResponse(statusLine, headers, body);
    }

    public String asString() {
        String header = String
            .join(" \r\n", List.of(statusLine.asString(), responseHeaders.asString()));
        return header + "\r\n" + body.asString();
    }
}
