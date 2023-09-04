package nextstep.jwp.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.common.HttpStatus;
import nextstep.jwp.common.HttpVersion;

public class HttpResponse {

    private static final String DELIMITER = "\r\n";

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final ResponseHeaders responseHeaders;
    private final String content;

    private HttpResponse(final HttpVersion httpVersion, final HttpStatus httpStatus,
                         final ResponseHeaders responseHeaders, final String content) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.responseHeaders = responseHeaders;
        this.content = content;
    }

    public static HttpResponse from(final ResponseEntity responseEntity) {
        final HttpVersion httpVersion = responseEntity.getHttpVersion();
        final HttpStatus httpStatus = responseEntity.getHttpStatus();
        final ResponseHeaders headers = ResponseHeaders.from(responseEntity.getHeaders());
        final String content = responseEntity.getContent();
        return new HttpResponse(httpVersion, httpStatus, headers, content);
    }

    public String toResponse() {
        return String.join(DELIMITER,
                httpVersion.getVersion() + " " + httpStatus.getStatusCode() + " " + httpStatus.name() + " ",
                makeHeaderResponse(),
                "",
                content);
    }

    private String makeHeaderResponse() {
        final Map<String, String> headers = responseHeaders.getHeaders();
        final List<String> headerStrings = headers.keySet()
                .stream()
                .map(key -> key + ": " + headers.get(key) + " ")
                .collect(Collectors.toList());

        return String.join(DELIMITER, headerStrings);
    }
}
