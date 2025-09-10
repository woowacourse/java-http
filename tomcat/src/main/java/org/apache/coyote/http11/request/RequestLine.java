package org.apache.coyote.http11.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestLine {

    private final HttpMethod method;
    private final String path;
    private final Map<String, String> queryParameters;
    private final String protocolVersion;

    public static RequestLine from(final String requestLine) {
        if(requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("잘못된 형식의 requestLine입니다: " + requestLine);
        }

        String[] requestLineParts = requestLine.split(" ");
        if (requestLineParts.length != 3) {
            throw new IllegalArgumentException("잘못된 형식의 requestLine입니다: " + requestLine);
        }
        String methodString = requestLineParts[0];
        String requestUri = requestLineParts[1];
        String protocolVersion = requestLineParts[2];

        String[] uriParts = requestUri.split("\\?");
        String path = uriParts[0];
        Map<String, String> queryParameters = new HashMap<>();
        if(uriParts.length==2) {
            queryParameters = extractQueryParameters(uriParts[1]);
        }

        return new RequestLine(HttpMethod.valueOf(methodString), path, queryParameters, protocolVersion);
    }

    private static Map<String, String> extractQueryParameters(final String queryString) {
        Map<String, String> queryParameters = new HashMap<>();

        for (String rawParam : queryString.split("&")) {
            String[] rawParamParts = rawParam.split("=");
            if(rawParamParts.length >= 2) {
                String key = URLDecoder.decode(rawParamParts[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(rawParamParts[1], StandardCharsets.UTF_8);
                queryParameters.put(key, value);
            }
        }
        return queryParameters;
    }
}
