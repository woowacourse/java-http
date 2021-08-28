package nextstep.jwp.framework.http.formatter;

import nextstep.jwp.framework.http.HttpHeader;
import nextstep.jwp.framework.http.HttpHeaders;
import nextstep.jwp.framework.http.HttpResponse;

public class HeaderLineFormatter extends AbstractLineFormatter {

    public static final String CRLF = "\r\n";
    public static final String HEADER_LINE_FORMAT = "%s: %s " + CRLF;

    private boolean passedEmptyLine = false;

    public HeaderLineFormatter(HttpResponse httpResponse) {
        super(httpResponse);
    }

    @Override
    public String transform() {
        final HttpHeaders httpHeaders = httpResponse.getHttpHeaders();
        final HttpHeader httpHeader = httpHeaders.poll();
        if (httpHeader == null) {
            passedEmptyLine = true;
            return CRLF;
        }

        return String.format(HEADER_LINE_FORMAT, httpHeader.getHeaderName(), httpHeader.getJoinedValue());
    }

    @Override
    public LineFormatter convertNextFormatter() {
        if (passedEmptyLine) {
            return new BodyLineFormatter(httpResponse);
        }
        return this;
    }
}
