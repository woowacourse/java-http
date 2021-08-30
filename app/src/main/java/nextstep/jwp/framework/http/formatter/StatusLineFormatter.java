package nextstep.jwp.framework.http.formatter;

import java.util.Objects;

import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.StatusLine;

public class StatusLineFormatter extends AbstractHttpFormatter {

    public static final String STATUS_LINE_FORMAT = "%s %s %s\r\n";

    private final HttpResponse httpResponse;

    public StatusLineFormatter(HttpResponse httpResponse) {
        super(httpResponse);
        this.httpResponse = Objects.requireNonNull(httpResponse);
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
    public HttpFormatter convertNextFormatter() {
        return new HeaderFormatter(httpResponse);
    }
}
