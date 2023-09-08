package nextstep.jwp.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    public static HttpResponse from(final ResponseEntity responseEntity) {
        final StatusLine statusLine = StatusLine.of(responseEntity.getHttpVersion(), responseEntity.getHttpStatus());
        final ResponseHeaders headers = ResponseHeaders.from(responseEntity.getHeaders());
        final String content = responseEntity.getContent();
        return new HttpResponse(statusLine, headers, content);
    }

    public String toResponse() {
        return String.join(DELIMITER,
                statusLine.toResponse(),
                responseHeaders.toResponse(),
                "",
                responseBody);
    }
}
