package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.List;
import org.apache.coyote.http11.Http11Header;

class Http11RequestHeaderParser {

    private static final String CRLF = "\r\n";

    private final Http11StartLineParser startLineParser;

    Http11RequestHeaderParser(Http11StartLineParser startLineParser) {
        this.startLineParser = startLineParser;
    }

    List<Http11Header> parseHeaders(String requestMessage) {
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
