package nextstep.jwp.framework.http.formatter;

import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.StatusLine;

public class StatusLineFormatter extends AbstractLineFormatter {

    public static final String STATUS_LINE_FORMAT = "%s %s %s\r\n";

    private final HttpResponse httpResponse;

    public StatusLineFormatter(HttpResponse httpResponse) {
        super(httpResponse);
        this.httpResponse = httpResponse;
    }

    @Override
    public String transform() {
        final StatusLine statusLine = httpResponse.getStatusLine();
        return String.format(STATUS_LINE_FORMAT,
                statusLine.getHttpVersion(),
                statusLine.getHttpStatusCode(),
                statusLine.getReasonPhrase());
    }

    @Override
    public LineFormatter convertNextFormatter() {
        return new HeaderLineFormatter(httpResponse);
    }
}
