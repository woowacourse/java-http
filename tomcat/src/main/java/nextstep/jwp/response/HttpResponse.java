package nextstep.jwp.response;

import java.util.Map;
import nextstep.jwp.common.HttpStatus;
import nextstep.jwp.common.HttpVersion;

public class HttpResponse {

    private static final String DELIMITER = "\r\n";

    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final String responseBody;

    private HttpResponse(final StatusLine statusLine, final ResponseHeaders responseHeaders,
                         final String responseBody) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final HttpVersion httpVersion, final HttpStatus httpStatus,
                                  final String responseBody,
                                  final Map<String, String> headers) {
        final StatusLine statusLine = StatusLine.of(httpVersion, httpStatus);
        final ResponseHeaders responseHeaders = ResponseHeaders.from(headers);
        return new HttpResponse(statusLine, responseHeaders, responseBody);
    }

    public String toResponse() {
        return String.join(DELIMITER,
                statusLine.toResponse(),
                responseHeaders.toResponse(),
                "",
                responseBody);
    }
}
