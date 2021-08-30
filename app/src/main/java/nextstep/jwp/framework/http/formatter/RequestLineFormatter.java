package nextstep.jwp.framework.http.formatter;

import java.util.Objects;

import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.RequestLine;

public class RequestLineFormatter extends AbstractHttpFormatter {

    public static final String REQUEST_LINE_FORMAT = "%s %s %s\r\n";

    private final HttpRequest httpRequest;

    public RequestLineFormatter(HttpRequest httpRequest) {
        super(httpRequest);
        this.httpRequest = Objects.requireNonNull(httpRequest);
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
    public HttpFormatter convertNextFormatter() {
        return new HeaderFormatter(httpRequest);
    }
}
