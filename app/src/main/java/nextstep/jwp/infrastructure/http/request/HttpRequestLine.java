package nextstep.jwp.infrastructure.http.request;

import java.util.Arrays;
import java.util.List;

public class HttpRequestLine {

    private static final String BLANK = " ";
    private static final int FIRST_LINE_ELEMENT_SIZE = 3;

    private final HttpMethod httpMethod;
    private final String path;
    private final String httpVersion;

    public HttpRequestLine(final HttpMethod httpMethod, final String path, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestLine of (String line) {
        final List<String> result = Arrays.asList(line.split(BLANK));

        if (result.size() != FIRST_LINE_ELEMENT_SIZE) {
            throw new IllegalArgumentException(String.format("Invalid HttpRequest Format.(%s)", line));
        }

        return new HttpRequestLine(HttpMethod.valueOf(result.get(0)), result.get(1), result.get(2));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
