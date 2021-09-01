package nextstep.jwp.http.http_request;

import com.google.common.net.HttpHeaders;
import nextstep.jwp.exception.NotFoundParamException;
import nextstep.jwp.http.common.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JwpHttpRequest {

    public static final Logger logger = LoggerFactory.getLogger(JwpHttpRequest.class);

    private static final String REQUEST_DELIMITER = " ";
    private static final String QUERY_STRING_SYMBOL = "?";
    private static final String PARAM_DELIMITER = "&";
    private static final String PARAM_KEY_AND_VALUE_DELIMITER = "=";

    private final JwpHttpMethod method;
    private final String uri;
    private final String httpVersion;
    private final Headers headers;
    private final Map<String, String> params;

    private JwpHttpRequest(JwpHttpMethod method, String uri, String httpVersion, Headers headers) {
        this(method, uri, httpVersion, headers, Collections.emptyMap());
    }

    private JwpHttpRequest(JwpHttpMethod method, String uri, String httpVersion, Headers headers, Map<String, String> params) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.params = params;
    }

    public static JwpHttpRequest of(BufferedReader reader) throws IOException {
        String requestHeader = reader.readLine();
        if (requestHeader == null) {
            throw new IllegalArgumentException("올바르지 않은 요청입니다.");
        }

        String[] requestInfos = requestHeader.split(REQUEST_DELIMITER);
        JwpHttpMethod requestMethod = JwpHttpMethod.of(requestInfos[0]);
        String requestUri = requestInfos[1];
        String requestHttpVersion = requestInfos[2];
        logger.info("Request METHOD: [{}] URI: {}", requestMethod, requestUri);

        Headers headers = Headers.of(reader);

        if (requestMethod.isGetRequest()) {
            return get(JwpHttpMethod.GET, requestUri, requestHttpVersion, headers);
        }

        if (requestMethod.isPostRequest()) {
            return post(reader, requestMethod, requestUri, requestHttpVersion, headers);
        }

        return new JwpHttpRequest(requestMethod, requestUri, requestHttpVersion, headers);
    }

    private static JwpHttpRequest get(JwpHttpMethod requestMethod, String requestUri, String requestHttpVersion, Headers headers) {
        if (!requestUri.contains(QUERY_STRING_SYMBOL)) {
            return new JwpHttpRequest(requestMethod, requestUri, requestHttpVersion, headers);
        }

        int index = requestUri.indexOf(QUERY_STRING_SYMBOL);
        String path = requestUri.substring(0, index);
        String queryString = requestUri.substring(index + 1);
        String[] queryParams = queryString.split(PARAM_DELIMITER);
        Map<String, String> params = parseParams(queryParams);
        return new JwpHttpRequest(requestMethod, path, requestHttpVersion, headers, params);
    }

    private static JwpHttpRequest post(BufferedReader reader, JwpHttpMethod requestMethod, String requestUri, String requestHttpVersion, Headers headers) throws IOException {
        if (headers.hasNoContent()) {
            return new JwpHttpRequest(requestMethod, requestUri, requestHttpVersion, headers);
        }

        int contentLength = Integer.parseInt(headers.getHeaderValue(HttpHeaders.CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = URLDecoder.decode(new String(buffer), "UTF-8");
        String[] jsonParams = requestBody.split(PARAM_DELIMITER);
        Map<String, String> params = parseParams(jsonParams);
        return new JwpHttpRequest(requestMethod, requestUri, requestHttpVersion, headers, params);
    }

    private static Map<String, String> parseParams(String[] queryParams) {
        Map<String, String> params = new HashMap<>();
        for (String queryParam : queryParams) {
            splitKeyAndValue(params, queryParam, PARAM_KEY_AND_VALUE_DELIMITER);
        }
        return params;
    }

    private static void splitKeyAndValue(Map<String, String> params, String queryParam, String delimiter) {
        String[] keyAndValue = queryParam.split(delimiter);
        params.put(keyAndValue[0].trim(), keyAndValue[1].trim());
    }

    public String getUri() {
        return uri;
    }

    public JwpHttpMethod getMethod() {
        return method;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getParam(String key) {
        if (!params.containsKey(key)) {
            throw new NotFoundParamException(key);
        }

        return params.get(key);
    }

    public boolean isGetRequest() {
        return method.isGetRequest();
    }

    public boolean isPostRequest() {
        return method.isPostRequest();
    }
}
