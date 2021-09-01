package nextstep.jwp.framework.infrastructure.http.request;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.framework.infrastructure.http.header.HttpHeaders;

public class OtherLines {

    private static final String CONTENT_LENGTH_DELIMITER = ": ";

    private final Map<HttpHeaders, String> otherLines;

    public OtherLines(Map<HttpHeaders, String> otherLines) {
        this.otherLines = otherLines;
    }

    public static OtherLines from(List<String> httpRequestHeaders) {
        Map<HttpHeaders, String> otherLines = new EnumMap<>(HttpHeaders.class);
        parseContentLength(otherLines, httpRequestHeaders);
        return new OtherLines(otherLines);
    }

    private static void parseContentLength(
        Map<HttpHeaders, String> otherLines,
        List<String> httpRequestHeaders
    ) {
        httpRequestHeaders.stream()
            .filter(header -> header.startsWith(HttpHeaders.CONTENT_LENGTH.getSignature()))
            .findAny()
            .ifPresentOrElse(header -> {
                    String contentLength = header.split(CONTENT_LENGTH_DELIMITER, -1)[1];
                    otherLines.put(HttpHeaders.CONTENT_LENGTH, contentLength);
                }, () -> otherLines.put(HttpHeaders.CONTENT_LENGTH, "0")
            );
    }

    public int getContentLength() {
        return Integer.parseInt(otherLines.get(HttpHeaders.CONTENT_LENGTH));
    }
}
