package nextstep.jwp.framework.http;

import java.util.Arrays;
import java.util.List;

public class HttpHeader {

    // General Header
    public static final String CONNECTION = "Connection";
    public static final String DATE = "Date";

    // Request Header
    public static final String AUTHORIZATION = "Authorization";
    public static final String COOKIE = "Cookie";
    public static final String USER_AGENT = "User-Agent";
    public static final String HOST = "Host";
    public static final String REFERER = "Referer";
    public static final String ORIGIN = "Origin";

    // Entity Header,
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";

    // Response Header,
    public static final String LOCATION = "Location";
    public static final String SERVER = "Server";
    public static final String SET_COOKIE = "Set-Cookie";

    private final String headerName;
    private final List<String> values;

    public HttpHeader(String headerName, String... values) {
        this(headerName, Arrays.asList(values));
    }

    public HttpHeader(String headerName, List<String> values) {
        this.headerName = headerName;
        this.values = values;
    }

    public String getHeaderName() {
        return headerName;
    }

    public String getJoinedValue() {
        return String.join(", ", values);
    }
}
