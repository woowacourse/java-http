package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String REQUEST_API_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String DOT = ".";
    private static final String QUERY_STRING_SYMBOL = "?";

    private final String method;
    private final String uri;
    private final String version;
    private final Map<String, String> headers = new HashMap<>();
    private Map<String, String> requestBody;

    private HttpRequest(final String method, final String uri, final String version) {
        validateMethod(method);
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    private void validateMethod(final String method) {
        if (method.equalsIgnoreCase(POST) ||
                method.equalsIgnoreCase(GET) ||
                method.equalsIgnoreCase(PUT) ||
                method.equalsIgnoreCase(DELETE)) {
            return;
        }
        throw new IllegalArgumentException("지원하지 않는 HTTP METHOD 입니다.");
    }

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        final String requestApi = bufferedReader.readLine();
        final String[] apiInfo = requestApi.split(REQUEST_API_DELIMITER);

        if (apiInfo.length != 3) {
            throw new IllegalArgumentException("잘못된 http 요청 입니다.");
        }

        final String requestMethod = apiInfo[HTTP_METHOD_INDEX];
        final String requestUri = apiInfo[REQUEST_URI_INDEX];
        final String requestVersion = apiInfo[HTTP_VERSION_INDEX];

        validateMethod(requestMethod);

        this.method = requestMethod;
        this.uri = requestUri;
        this.version = requestVersion;
        initHeaders(bufferedReader);
        initRequestBody(bufferedReader);
    }

    private void initHeaders(final BufferedReader bufferedReader) throws IOException {
        String line = "";

        while(!(line = bufferedReader.readLine()).isBlank()) {
            final String[] headerInfo = line.split(":");
            final String headerName = headerInfo[0];
            final String value = headerInfo[1].trim();
            headers.put(headerName, value);
        }
    }

    private void initRequestBody(final BufferedReader bufferedReader) throws IOException {
        if (headers.containsKey("Content-Length")) {
            this.requestBody = new HashMap<>();
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String requestBodyBuffer = new String(buffer);

            final String[] requestBodies = requestBodyBuffer.split("&");
            for (String body : requestBodies) {
                final String[] requestBodyInfo = body.split("=");
                final String requestBodyName = requestBodyInfo[0];
                final String requestBodyValue = requestBodyInfo[1];
                this.requestBody.put(requestBodyName, requestBodyValue);
            }
        }
    }

    public static HttpRequest toIndex() {
        return new HttpRequest(GET, "/index.html", "HTTP/1.1");
    }

    public String getUri() {
        if (hasQueryString()) {
            final int queryIndex = uri.indexOf(QUERY_STRING_SYMBOL);
            return uri.substring(0, queryIndex);
        }
        return uri;
    }

    public boolean hasQueryString() {
        return uri.contains(QUERY_STRING_SYMBOL);
    }

    public boolean isStaticRequest() {
        return uri.contains(DOT) || uri.equals("/");
    }

    public String getExtension() {
        final int dotIndex = uri.indexOf(DOT);
        return uri.substring(dotIndex + 1);
    }

    public Map<String, String> getQueryString() {
        if (!hasQueryString()) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>();
        final int queryIndex = uri.indexOf(QUERY_STRING_SYMBOL);
        final String[] queryParameters = uri.substring(queryIndex + 1).split("&");
        for (String queryParameter : queryParameters) {
            final String[] queryKeyAndValue = queryParameter.split("=");
            if (queryKeyAndValue.length != 2) {
                throw new IllegalArgumentException("잘못된 Query String 입니다.");
            }
            final String key = queryKeyAndValue[0];
            final String value = queryKeyAndValue[1];
            result.put(key, value);
        }
        return result;
    }

    public boolean isGetRequest() {
        return method.equalsIgnoreCase(GET);
    }

    public boolean hasRequestBody() {
        return requestBody != null;
    }

    public Map<String, String> getRequestBody() {
        return requestBody;
    }
}
