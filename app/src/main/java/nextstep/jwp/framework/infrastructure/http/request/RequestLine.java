package nextstep.jwp.framework.infrastructure.http.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.protocol.Protocol;

public class RequestLine {

    private static final String WHITE_SPACE_DELIMITER = " ";
    private static final String QUERY_STRING_START_CHARACTER = "?";
    private static final String QUERY_PARAMETER_DELIMITER = "&";
    private static final String QUERY_PARAMETER_KEY_VALUE_DELIMITER = "=";
    private static final int VALID_START_LINE_CHUNK_SIZE = 3;

    private final HttpMethod httpMethod;
    private final String url;
    private final Protocol protocol;
    private final Map<String, String> queryParameters;

    public RequestLine(
        HttpMethod httpMethod,
        String url,
        Protocol protocol,
        Map<String, String> queryParameters
    ) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.protocol = protocol;
        this.queryParameters = queryParameters;
    }

    public static RequestLine from(String requestLine) {
        String[] split = requestLine.split(WHITE_SPACE_DELIMITER);
        if (split.length != VALID_START_LINE_CHUNK_SIZE) {
            throw new IllegalStateException("Invalid Http Request Line");
        }
        HttpMethod httpMethod = HttpMethod.valueOf(split[0]);
        String url = split[1];
        Protocol protocol = Protocol.findProtocol(split[2]);
        Map<String, String> queryParameters = parseQueryParameters(url);
        return new RequestLine(httpMethod, url, protocol, queryParameters);
    }

    private static Map<String, String> parseQueryParameters(String url) {
        int index = url.lastIndexOf(QUERY_STRING_START_CHARACTER);
        if (index < 0) {
            return new HashMap<>();
        }
        String queryString = url.substring(index + 1);
        return Arrays.stream(queryString.split(QUERY_PARAMETER_DELIMITER))
            .map(keyAndValue -> keyAndValue.split(QUERY_PARAMETER_KEY_VALUE_DELIMITER, -1))
            .collect(Collectors.toMap(split -> split[0], split -> split[1]));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }
}
