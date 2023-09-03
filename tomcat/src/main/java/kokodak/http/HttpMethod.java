package kokodak.http;

import static kokodak.Constants.BLANK;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST,
    ;

    public static HttpMethod from(final String startLine) {
        final String httpMethod = startLine.split(BLANK.getValue())[0];
        return Arrays.stream(values())
                     .filter(hm -> hm.name().equals(httpMethod))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Invalid HTTP Method"));
    }
}
