package nextstep.jwp.http.request;

import java.util.Map;

public class HttpRequestHeader {

    private static final String START_LINE = "START_LINE";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final int NONE_QUERY = -1;

    private final Map<String, String> headerLines;

    public HttpRequestHeader(Map<String, String> headerLines) {
        this.headerLines = headerLines;
    }

    public String method() {
        final String firstLine = headerLines.get(START_LINE);
        final String[] splitFirstLine = firstLine.split(" ");
        return splitFirstLine[0];
    }

    public String getRequestURLWithoutQuery() {
        final String firstLine = headerLines.get(START_LINE);
        final String[] splitFirstLine = firstLine.split(" ");
        final String rawURL = splitFirstLine[1];
        final int index = rawURL.indexOf("?");

        if (index == NONE_QUERY) {
            return rawURL;
        }
        return rawURL.substring(0, index);
    }

    public boolean isResource() {
        final String firstLine = headerLines.get(START_LINE);
        final String[] splitFirstLine = firstLine.split(" ");
        final String rawURL = splitFirstLine[1];
        String[] splitURL = rawURL.split("\\.");
        return splitURL.length != 1;
    }

    public String resourceType() {
        final String firstLine = headerLines.get(START_LINE);
        final String[] splitFirstLine = firstLine.split(" ");
        final String rawURL = splitFirstLine[1];
        String[] splitURL = rawURL.split("\\.");
        return splitURL[splitURL.length - 1];
    }

    public int contentLength() {
        return Integer.parseInt(headerLines.get(CONTENT_LENGTH));
    }
}
