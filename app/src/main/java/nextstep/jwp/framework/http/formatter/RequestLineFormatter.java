package nextstep.jwp.framework.http.formatter;

import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.RequestLine;

public class RequestLineFormatter extends AbstractLineFormatter {

    public static final String REQUEST_LINE_FORMAT = "%s %s %s\r\n";

    private final HttpRequest httpRequest;

    public RequestLineFormatter(HttpRequest httpRequest) {
        super(httpRequest);
        this.httpRequest = httpRequest;
    }

    @Override
    public String transform() {
        final RequestLine requestLine = httpRequest.getRequestLine();
        return String.format(REQUEST_LINE_FORMAT,
                requestLine.getMethod(),
                requestLine.getPath(),
                requestLine.getVersion());
    }

    @Override
    public LineFormatter convertNextFormatter() {
        return new HeaderLineFormatter(httpRequest);
    }
}
