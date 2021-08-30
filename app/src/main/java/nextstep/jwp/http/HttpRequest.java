package nextstep.jwp.http;

import java.util.List;
import java.util.Map;

public class HttpRequest {
    public static final String QUERY_STRING_DELIMITER = "&";
    public static final String QUERY_KEY_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private final HttpRequestHeader header;
    private final HttpRequestBody body;

    public HttpRequest(final HttpRequestHeader header, final HttpRequestBody body) {
        this.header = header;
        this.body = body;
    }

    public static HttpRequest ofStaticFile(final String url) {
        return new HttpRequest(
                new HttpRequestHeader(List.of("GET " + url + " HTTP/1.1 ")),
                null
        );
    }

    public boolean isGet() {
        return HttpMethod.isGet(getHttpMethod());
    }

    public String getHttpMethod() {
        return header.getHttpMethod().toUpperCase();
    }

    public String getPath() {
        return header.getPath();
    }

    public Map<String, String> getQueryParameters() {
        return header.getQueryParameters();
    }

    public String getProtocol() {
        return header.getProtocol();
    }

    public Map<String, String> getPayload() {
        return body.getPayload();
    }
}
