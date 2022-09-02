package http;

import java.util.Arrays;
import java.util.Objects;

public enum HttpHeader {

    METHOD("Method"),
    REQUEST_URI("RequestURI"),
    PROTOCOL("Protocol"),
    HOST("Host"),
    CONNECTION("Connection"),
    ;

    private final String headerName;

    HttpHeader(final String headerName) {
        this.headerName = headerName;
    }

    public static HttpHeader from(String httpHeader) {
        return Arrays.stream(values())
                .filter(value -> Objects.equals(value.headerName.toUpperCase(), httpHeader.toUpperCase()))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String getName() {
        return headerName;
    }
}
