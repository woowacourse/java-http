package kokodak;

import static kokodak.Constants.BLANK;

import java.util.Arrays;

public enum HttpVersion {

    HTTP11("HTTP/1.1"),
    ;

    private String value;

    HttpVersion(final String value) {
        this.value = value;
    }

    public static HttpVersion from(final String startLine) {
        final String httpVersion = startLine.split(BLANK.getValue())[2];
        return Arrays.stream(values())
                     .filter(hv -> hv.getValue().equals(httpVersion))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Invalid HTTP Version"));
    }

    public String getValue() {
        return value;
    }
}
