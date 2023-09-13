package nextstep.org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import nextstep.org.apache.catalina.cookie.Cookies;
import nextstep.org.apache.coyote.http11.HttpHeader;
import nextstep.org.apache.coyote.http11.Status;

public class Http11Response {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String HEADER_FORMAT = "%s: %s ";
    private static final String EMPTY_LINE = "";
    private static final String LINEBREAK_DELIMITER = "\r\n";

    private StatusLine statusLine;
    private Map<HttpHeader, String> headers = new LinkedHashMap<>();
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
                .map(entry -> String.format(HEADER_FORMAT, entry.getKey().getValue(), entry.getValue()))
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

    public Http11Response setHeader(HttpHeader httpHeader, String value) {
        this.headers.put(httpHeader, value);
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
