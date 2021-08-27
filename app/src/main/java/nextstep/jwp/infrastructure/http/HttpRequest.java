package nextstep.jwp.infrastructure.http;

import java.util.ArrayList;
import java.util.List;

public class HttpRequest {

    private static final String LAST_HEADER = "";

    private final HttpRequestLine requestLine;
    private final HttpHeaders headers;
    private final String messageBody;

    public HttpRequest(final HttpRequestLine requestLine, final HttpHeaders headers, final String messageBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    public static HttpRequest of(final List<String> httpRequest) {
        final HttpRequestLine requestLine = HttpRequestLine.of(httpRequest.get(0));
        final HttpHeaders httpHeaders = HttpHeaders.of(findHeadersFromRequest(httpRequest));
        final String httpMessageBody = findMessageBodyFromRequest(httpRequest);

        return new HttpRequest(requestLine, httpHeaders, httpMessageBody);
    }

    private static List<String> findHeadersFromRequest(final List<String> httpRequest) {
        final List<String> headers = new ArrayList<>();

        for (String line : httpRequest.subList(1, httpRequest.size())) {
            if (LAST_HEADER.equals(line)) {
                break;
            }

            headers.add(line);
        }

        return headers;
    }

    private static String findMessageBodyFromRequest(final List<String> httpRequest) {
        if (!httpRequest.contains(LAST_HEADER)) {
            return "";
        }

        final int messageBodyStartIndex = httpRequest.indexOf(LAST_HEADER) + 1;
        return String.join(System.lineSeparator(), httpRequest.subList(messageBodyStartIndex, httpRequest.size()));
    }

    public HttpRequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
