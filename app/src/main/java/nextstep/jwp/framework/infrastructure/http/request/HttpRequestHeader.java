package nextstep.jwp.framework.infrastructure.http.request;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.protocol.Protocol;

public class HttpRequestHeader {

    private static final String WHITE_SPACE_DELIMITER = " ";
    private static final String QUERY_STRING_START_CHARACTER = "?";
    private static final String QUERY_PARAMETER_DELIMITER = "&";
    private static final String QUERY_PARAMETER_KEY_VALUE_DELIMITER = "=";
    private static final String CONTENT_LENGTH_DELIMITER = ": ";
    private static final int VALID_START_LINE_CHUNK_SIZE = 3;
    private static final int COUNTS_WHEN_VALUE_NULL = 1;

    private final HttpMethod httpMethod;
    private final String url;
    private final Protocol protocol;
    private final Map<String, String> queryParameters;
    private final int contentLength;

    public HttpRequestHeader(
        HttpMethod httpMethod,
        String url,
        Protocol protocol,
        Map<String, String> queryParameters,
        int contentLength
    ) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.protocol = protocol;
        this.queryParameters = queryParameters;
        this.contentLength = contentLength;
    }

    public static HttpRequestHeader from(List<String> httpRequestHeaders) {
        if (httpRequestHeaders.isEmpty()) {
            throw new IllegalStateException("Invalid Http Request Headers");
        }
        String httpRequestStartLine = httpRequestHeaders.get(0);
        String[] split = httpRequestStartLine.split(WHITE_SPACE_DELIMITER);
        if (split.length != VALID_START_LINE_CHUNK_SIZE) {
            throw new IllegalStateException("Invalid Http Request Start Line");
        }
        HttpMethod httpMethod = HttpMethod.valueOf(split[0]);
        Protocol protocol = Protocol.findProtocol(split[2]);
        String url = split[1];
        Map<String, String> queryParameters = parseQueryParameters(url);
        int contentLength = parseContentLength(httpRequestHeaders);
        return new HttpRequestHeader(httpMethod, url, protocol, queryParameters, contentLength);
    }

    private static Map<String, String> parseQueryParameters(String url) {
        Map<String, String> queryParameters = new LinkedHashMap<>();
        int index = url.lastIndexOf(QUERY_STRING_START_CHARACTER);
        String queryString = url.substring(index + 1);
        if (queryString.isEmpty()) {
            return queryParameters;
        }
        String[] splitParameters = queryString.split(QUERY_PARAMETER_DELIMITER);
        for (String queryParameter : splitParameters) {
            String[] split = queryParameter.split(QUERY_PARAMETER_KEY_VALUE_DELIMITER, -1);
            addQueryParameters(split, queryParameters);
        }
        return queryParameters;
    }

    private static void addQueryParameters(String[] split, Map<String, String> queryParameters) {
        if (split.length == COUNTS_WHEN_VALUE_NULL) {
            return;
        }
        String key = split[0];
        String value = split[1];
        queryParameters.put(key, value);
    }

    private static int parseContentLength(List<String> httpRequestHeaders) {
        Optional<String> contentLengthHeader = httpRequestHeaders.stream()
            .filter(header -> header.startsWith("Content-Length:"))
            .findAny();
        if (contentLengthHeader.isEmpty()) {
            return 0;
        }
        String[] split = contentLengthHeader.orElseThrow(IllegalStateException::new)
            .split(CONTENT_LENGTH_DELIMITER);
        if (split.length != 2) {
            throw new IllegalStateException("Invalid Content Length");
        }
        return Integer.parseInt(split[1]);
    }

    public HttpMethod getMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public int getContentLength() {
        return contentLength;
    }
}
