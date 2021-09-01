package nextstep.jwp.framework.infrastructure.http.response;

import java.util.Map;
import nextstep.jwp.framework.infrastructure.http.header.HttpHeaders;

public class OtherResponseLines {

    private final Map<HttpHeaders, String> otherLines;

    public OtherResponseLines(Map<HttpHeaders, String> otherLines) {
        this.otherLines = otherLines;
    }

    public void add(HttpHeaders httpHeaders, String value) {
        otherLines.put(httpHeaders, value);
    }

    public String get(HttpHeaders httpHeaders) {
        return otherLines.get(httpHeaders);
    }

    public Map<HttpHeaders, String> getOtherLines() {
        return otherLines;
    }
}
