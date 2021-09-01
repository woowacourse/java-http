package nextstep.jwp.framework.infrastructure.http.request;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.framework.infrastructure.http.header.HttpHeaders;

public class OtherRequestLines {

    private static final String CONTENT_LENGTH_DELIMITER = ": ";

    private final Map<HttpHeaders, String> otherLines;

    public OtherRequestLines(Map<HttpHeaders, String> otherLines) {
        this.otherLines = otherLines;
    }

    public static OtherRequestLines from(List<String> httpRequestHeaders) {
        Map<HttpHeaders, String> otherLines = new EnumMap<>(HttpHeaders.class);
        parseContentLength(otherLines, httpRequestHeaders);
        return new OtherRequestLines(otherLines);
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

    public String get(HttpHeaders httpHeaders) {
        return otherLines.get(httpHeaders);
    }
}
