package nextstep.jwp.http;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String PROTOCOL = "HTTP/1.1";
    private static final String FINAL_HEADER_ELEMENT = "";
    private static final String BLANK = " ";
    private static final String HEADER_DELIMITER = ": ";

    private final ResponseStatus responseStatus;

    private final HttpHeader httpHeader;

    private final String responseBody;

    private HttpResponse(ResponseStatus responseStatus, HttpHeader httpHeader, String responseBody) {
        this.responseStatus = responseStatus;
        this.httpHeader = httpHeader;
        this.responseBody = responseBody;
    }

    public static HttpResponse status(ResponseStatus responseStatus, HttpHeader httpHeader, String responseBody) {
        return new HttpResponse(responseStatus, httpHeader, responseBody);
    }

    public static HttpResponse status(ResponseStatus responseStatus, HttpHeader httpHeader) {
        return new HttpResponse(responseStatus, httpHeader, null);
    }

    public String toResponseMessage() {
        return String.join(System.lineSeparator(),
            getStartLine(),
            getHeaderLines(),
            responseBody);
    }

    private String getStartLine() {
        return String.join(BLANK,
            PROTOCOL, responseStatus.getStatusCode().toString(), responseStatus.getStatusMessage());
    }

    private String getHeaderLines() {
        Map<String, String> allHeaders = this.httpHeader.getAllHeaders();
        List<String> headerMessage = allHeaders.keySet().stream()
            .map(key -> String.join(HEADER_DELIMITER, key, allHeaders.get(key)) + BLANK)
            .collect(Collectors.toList());

        return String.join(System.lineSeparator(), headerMessage) + System.lineSeparator();
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
