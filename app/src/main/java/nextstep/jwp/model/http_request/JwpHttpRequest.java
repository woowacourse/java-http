package nextstep.jwp.model.http_request;

import nextstep.jwp.exception.NotFoundParamException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JwpHttpRequest {

    private static final String REQUEST_DELIMITER = " ";
    private static final String HEADER_DELIMITER = ": ";
    private static final String QUERY_STRING_SYMBOL = "?";
    private static final String QUERY_PARAM_KEY_AND_VALUE_DELIMITER = "=";

    private final String method;
    private final String uri;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final Map<String, String> params;

    public JwpHttpRequest(String method, String uri, String httpVersion, Map<String, String> headers, Map<String, String> params) {
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
        String requestMethod = requestInfos[0];
        String requestUri = requestInfos[1];
        String requestHttpVersion = requestInfos[2];

        Map<String, String> headers = extractedHeaders(reader);

        if (requestMethod.equals("GET")) {
            if (!requestUri.contains(QUERY_STRING_SYMBOL)) {
                return new JwpHttpRequest(requestMethod, requestUri, requestHttpVersion, headers, Collections.EMPTY_MAP);
            }

            int index = requestUri.indexOf(QUERY_STRING_SYMBOL);
            String path = requestUri.substring(0, index);
            String queryString = requestUri.substring(index + 1);
            String[] queryParams = queryString.split("&");

            Map<String, String> params = new HashMap<>();
            for (String queryParam : queryParams) {
                splitKeyAndValue(params, queryParam, QUERY_PARAM_KEY_AND_VALUE_DELIMITER);
            }
            return new JwpHttpRequest(requestMethod, path, requestHttpVersion, headers, params);
        }

        // todo: 'POST 방식으로 회원가입' 기능 구현

        return new JwpHttpRequest(requestMethod, requestUri, requestHttpVersion, headers, Collections.EMPTY_MAP); //todo: PARAMS 값 넣기!
    }

    private static Map<String, String> extractedHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line = reader.readLine();
        while (!line.isBlank()) {
            splitKeyAndValue(headers, line, HEADER_DELIMITER);
//            String[] headerInfo = line.split(HEADER_DELIMITER); todo: 잘 동작하면 지우기!
//            headers.put(headerInfo[0], headerInfo[1]);
            line = reader.readLine();
        }

        return headers;
    }

    private static void splitKeyAndValue(Map<String, String> params, String queryParam, String delimiter) {
        String[] keyAndValue = queryParam.split(delimiter);
        params.put(keyAndValue[0], keyAndValue[1]);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public boolean isEmptyParams() {
        return params.isEmpty();
    }

    public String getParam(String key) {
        if (!params.containsKey(key)) {
            throw new NotFoundParamException(key);
        }

        return params.get(key);
    }
}
