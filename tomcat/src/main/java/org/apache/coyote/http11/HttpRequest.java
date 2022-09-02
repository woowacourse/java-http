package org.apache.coyote.http11;

import org.apache.coyote.http11.exception.InvalidHttpRequestStartLineException;
import java.util.List;

public class HttpRequest {

    private static final String HOST_PREFIX = "Host: ";
    private static final String ACCEPT_PREFIX = "Accept: ";
    private static final String CONNECTION_PREFIX = "Connection: ";
    private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";

    private final String startLine;
    private final String host;
    private final String accept;
    private final String connection;

    private HttpRequest(final String startLine, final String host, final String accept, final String connection) {
        this.startLine = startLine;
        this.host = host;
        this.accept = accept;
        this.connection = connection;
    }


    public static HttpRequest of(final String startLine, final List<String> lines) {
        if (!RequestMethod.isIn(startLine)) {
            throw new InvalidHttpRequestStartLineException();
        }

        final String host = extractByPrefix(lines, HOST_PREFIX);
        final String acceptType = extractByPrefix(lines, ACCEPT_PREFIX);
        final String connection = extractByPrefix(lines, CONNECTION_PREFIX);

        return new HttpRequest(startLine, host, acceptType, connection);
    }

    private static String extractByPrefix(final List<String> lines, final String prefix) {
        final String lineByPrefix = lines.stream()
                .filter(line -> line.contains(prefix))
                .findAny()
                .orElse(null);

        if (lineByPrefix == null) {
            return null;
        }

        return lineByPrefix.split(prefix)[1];
    }

    public String getUri() {
        return startLine.split(" ")[1];
    }

    public String getAcceptType() {
        if (accept == null) {
            return TEXT_HTML_CHARSET_UTF_8;
        }

        return accept.split(",")[0];
    }

    @Override
    public String toString() {
        return "Request{" +
                "startLine='" + startLine + '\'' +
                ", host='" + host + '\'' +
                ", acceptType='" + accept + '\'' +
                ", connection='" + connection + '\'' +
                '}';
    }
}
