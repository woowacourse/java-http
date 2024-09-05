package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.List;

public class Http11HeaderParser {

    private static final String CRLF = "\r\n";

    private final Http11StartLineParser startLineParser;

    public Http11HeaderParser(Http11StartLineParser startLineParser) {
        this.startLineParser = startLineParser;
    }

    public List<Http11Header> parseHeaders(String requestMessage) {
        String rawHeaders = requestMessage.split(CRLF + CRLF)[0]
                .replace(startLineParser.parseStartLine(requestMessage), "")
                .replaceFirst(CRLF, "");

        return Arrays.stream(rawHeaders.split(CRLF))
                .filter(rawHeader -> !rawHeader.startsWith("Cookie"))
                .map(rawHeader -> {
                    String[] split = rawHeader.split(":");
                    String key = split[0].trim();
                    String value = split[1].trim();
                    return new Http11Header(key, value);
                })
                .toList();
    }
}
