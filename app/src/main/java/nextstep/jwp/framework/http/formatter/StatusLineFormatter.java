package nextstep.jwp.framework.http.formatter;

import nextstep.jwp.framework.http.HttpMessage;
import nextstep.jwp.framework.http.StatusLine;

public class StatusLineFormatter implements HttpFormatter {

    public static final String STATUS_LINE_FORMAT = "%s %s %s\r\n";

    private final StatusLine statusLine;

    public StatusLineFormatter(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    @Override
    public String transform() {
        return String.format(STATUS_LINE_FORMAT,
                statusLine.getHttpVersion(),
                statusLine.getHttpStatusCode(),
                statusLine.getReasonPhrase());
    }

    @Override
    public HttpFormatter convertNextFormatter(HttpMessage httpMessage) {
        return new HeaderFormatter(httpMessage.getHttpHeaders());
    }
}
