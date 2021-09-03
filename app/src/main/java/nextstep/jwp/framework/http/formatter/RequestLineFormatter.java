package nextstep.jwp.framework.http.formatter;

import nextstep.jwp.framework.http.HttpMessage;
import nextstep.jwp.framework.http.RequestLine;

public class RequestLineFormatter implements HttpFormatter {

    public static final String REQUEST_LINE_FORMAT = "%s %s %s\r\n";

    private final RequestLine requestLine;

    public RequestLineFormatter(RequestLine requestLine) {
        this.requestLine = requestLine;
    }

    @Override
    public String transform() {
        return String.format(REQUEST_LINE_FORMAT,
                requestLine.getMethod(),
                requestLine.getPath(),
                requestLine.getVersion());
    }

    @Override
    public HttpFormatter convertNextFormatter(HttpMessage httpMessage) {
        return new HeaderFormatter(httpMessage.getHttpHeaders());
    }
}
