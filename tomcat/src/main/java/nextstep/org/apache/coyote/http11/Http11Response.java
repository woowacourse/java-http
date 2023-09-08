package nextstep.org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Http11Response {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String HEADER_FORMAT = "%s: %s ";
    private static final String EMPTY_LINE = "";
    private static final String LINEBREAK_DELIMITER = "\r\n";

    private StatusLine statusLine;
    private Map<String, String> headers = new LinkedHashMap<>();
    private Cookies cookies = null;
    private String body = null;

    public Http11Response(Status status) {
        this.statusLine = new StatusLine(HTTP_VERSION, status);
    }

    public byte[] createResponseAsByteArray() {
        return createResponse().getBytes();
    }

    private String createResponse() {
        String response = String.join(LINEBREAK_DELIMITER,
                statusLine.toString(),
                createHeadersResponse(),
                EMPTY_LINE
        );
        if (Objects.nonNull(body)) {
            response += LINEBREAK_DELIMITER + body;
        }
        return response;
    }

    private String createHeadersResponse() {
        String headersResponse = this.headers.entrySet().stream()
                .map(entry -> String.format(HEADER_FORMAT, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(LINEBREAK_DELIMITER));
        if (Objects.nonNull(cookies) && !cookies.isEmpty()) {
            headersResponse += LINEBREAK_DELIMITER + cookies.createSetCookieHeader();
        }
        return headersResponse;
    }

    public Http11Response setStatus(Status status) {
        this.statusLine = new StatusLine(HTTP_VERSION, status);
        return this;
    }

    public Http11Response setHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public Http11Response setCookies(Cookies cookies) {
        this.cookies = cookies;
        return this;
    }

    public Http11Response setBody(String body) {
        this.body = body;
        return this;
    }
}
