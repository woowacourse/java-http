package nextstep.jwp.http.reqeust;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpRequestLine {

    private static final String REQUEST_LINE_SEPARATOR = " ";
    private static final int REQUEST_LINE_CONTENT_COUNT = 3;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private String method;
    private String path;
    private QueryParams queryParams;
    private String version;

    public HttpRequestLine(final String method, final URI uri, final String version) {
        this.method = method;
        this.path = uri.getPath();
        this.queryParams = new QueryParams(uri.getQuery());
        this.version = version;
    }

    public static HttpRequestLine from(final String requestLine) {
        String[] line = requestLine.split(REQUEST_LINE_SEPARATOR);
        validateRequestLineFormat(line);

        String method = line[METHOD_INDEX];
        URI uri = requestUri(line[URI_INDEX]);
        String version = line[VERSION_INDEX];

        return new HttpRequestLine(method, uri, version);
    }

    private static void validateRequestLineFormat(final String[] line) {
        if (line.length != REQUEST_LINE_CONTENT_COUNT) {
            throw new IllegalArgumentException();
        }
    }

    private static URI requestUri(final String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException();
        }
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }
}
