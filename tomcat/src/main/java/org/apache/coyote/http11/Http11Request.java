package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Http11Request {

    private final StartLine startLine;
    private final List<Header> headers;
    private final String body;

    public Http11Request(final List<String> lines, final String body) {
        this.startLine = extractStartLine(lines.getFirst());
        this.headers = extractHeaders(lines);
        this.body = body;
    }

    public String extractStaticPath() {
        return startLine.extractStaticPath();
    }

    public String getUri() {
        return startLine.getUri();
    }

    public Map<String, String> extractRequestBody() {
        return Arrays.stream(body.split("&"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
    }

    public boolean isStatic() {
        return startLine.isStatic();
    }

    private List<Header> extractHeaders(final List<String> lines) {
        List<Header> headerList = new ArrayList<>();

        for (int i = 1; i < lines.size(); i ++) {
            String line = lines.get(i);
            String[] splittedLines = line.split(":", 2);
            if (splittedLines.length == 2) {
                headerList.add(new Header(splittedLines[0].trim(), splittedLines[1].trim()));
            }
        }

        return headerList;
    }

    private StartLine extractStartLine(final String startLine) {
        final String[] startLineValues = startLine.split(" ");

        validateStartLineCounts(startLineValues);

        final HttpMethod httpMethod = HttpMethod.parseHttpMethodFrom(startLineValues[0]);
        final String uri = startLineValues[1];
        final HttpVersion httpVersion = HttpVersion.parseHttpVersionFrom(startLineValues[2]);

        return new StartLine(httpMethod, uri, httpVersion);
    }

    private void validateStartLineCounts(final String[] startLineValues) {
        if (startLineValues.length != 3) {
            throw new IllegalArgumentException("StartLine의 3개의 값이 아닙니다.");
        }
    }
}
