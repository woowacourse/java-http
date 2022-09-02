package org.apache.coyote.http11;

import org.apache.coyote.http11.exception.InvalidHttpRequestStartLineException;
import java.util.List;

public class Request {

    private static final String HOST_PREFIX = "Host: ";
    private static final String ACCEPT_PREFIX = "Accept: ";
    private static final String CONNECTION_PREFIX = "Connection: ";
    private final String startLine;
    private final String host;
    private final String acceptType;
    private final String connection;

    public Request(final String startLine, final String host, final String acceptType, final String connection) {
        this.startLine = startLine;
        this.host = host;
        this.acceptType = acceptType;
        this.connection = connection;
    }


    public static Request of(final String startLine, final List<String> lines) {
        if (!RequestMethod.isIn(startLine)) {
            throw new InvalidHttpRequestStartLineException();
        }

        final String host = extractByPrefix(lines, HOST_PREFIX);
        final String acceptType = extractByPrefix(lines, ACCEPT_PREFIX);
        final String connection = extractByPrefix(lines, CONNECTION_PREFIX);

        return new Request(startLine, host, acceptType, connection);
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

    public String getStartLine() {
        return startLine;
    }

    public String getHost() {
        return host;
    }

    public String getAcceptType() {
        return acceptType;
    }

    public String getConnection() {
        return connection;
    }

    @Override
    public String toString() {
        return "Request{" +
                "startLine='" + startLine + '\'' +
                ", host='" + host + '\'' +
                ", acceptType='" + acceptType + '\'' +
                ", connection='" + connection + '\'' +
                '}';
    }
}
