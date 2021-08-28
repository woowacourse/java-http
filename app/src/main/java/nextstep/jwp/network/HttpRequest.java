package nextstep.jwp.network;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HttpRequest {

    private static final String CRLF = "\r\n";

    private final RequestLine requestLine;
    private final List<String> headers;
    // for now it's string but might have to be generic
    private final List<String> messageBody;

    private HttpRequest(RequestLine requestLine, List<String> headers, List<String> messageBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    public static HttpRequest of(List<String> requestAsString) {
        final RequestLine requestLine = RequestLine.of(requestAsString.get(0));
        int separatorLine = IntStream.range(1, requestAsString.size())
                .filter(i -> CRLF.equals(requestAsString.get(i)))
                .findFirst()
                .orElse(requestAsString.size());
        final List<String> headers = IntStream.range(1, separatorLine - 1)
                .mapToObj(requestAsString::get)
                .collect(Collectors.toList());
        final List<String> messageBody = IntStream.range(separatorLine + 1, requestAsString.size())
                .mapToObj(requestAsString::get)
                .collect(Collectors.toList());
        return new HttpRequest(requestLine, headers, messageBody);
    }

    public URI toURI() {
        return requestLine.getURI();
    }
}
