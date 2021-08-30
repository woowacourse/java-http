package nextstep.jwp.framework.http.formatter;

import nextstep.jwp.framework.http.HttpHeaders;
import nextstep.jwp.framework.http.HttpMessage;

public class HeaderFormatter extends AbstractHttpFormatter {

    private static final String CRLF = "\r\n";

    private static final String HEADER_LINE_FORMAT = "%s: %s " + CRLF;

    public HeaderFormatter(HttpMessage httpMessage) {
        super(httpMessage);
    }

    @Override
    public String transform() {
        final HttpHeaders httpHeaders = httpMessage.getHttpHeaders();
        StringBuilder headers = new StringBuilder();
        httpHeaders.getHeaders()
                   .forEach((key, value) -> headers.append(String.format(HEADER_LINE_FORMAT, key, value)));
        return headers + CRLF;
    }

    @Override
    public HttpFormatter convertNextFormatter() {
        return new BodyFormatter(httpMessage);
    }
}
