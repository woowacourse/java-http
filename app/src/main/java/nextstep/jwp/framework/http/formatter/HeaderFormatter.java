package nextstep.jwp.framework.http.formatter;

import nextstep.jwp.framework.http.HttpHeaders;
import nextstep.jwp.framework.http.HttpMessage;

public class HeaderFormatter implements HttpFormatter {

    private static final String CRLF = "\r\n";

    private static final String HEADER_LINE_FORMAT = "%s: %s " + CRLF;

    private final HttpHeaders httpHeaders;

    public HeaderFormatter(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    @Override
    public String transform() {
        StringBuilder headers = new StringBuilder();
        httpHeaders.getHeaders()
                   .forEach((key, value) -> headers.append(String.format(HEADER_LINE_FORMAT, key, value)));
        return headers + CRLF;
    }

    @Override
    public HttpFormatter convertNextFormatter(HttpMessage httpMessage) {
        return new BodyFormatter(httpMessage.getBody());
    }
}
