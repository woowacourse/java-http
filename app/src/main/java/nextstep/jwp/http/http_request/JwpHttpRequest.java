package nextstep.jwp.http.http_request;

import com.google.common.net.HttpHeaders;
import nextstep.jwp.http.common.Headers;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JwpHttpRequest {

    private static final String HEADER_DELIMITER = ": ";
    private static final String REQUEST_DELIMITER = " ";

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody requestBody;

    public JwpHttpRequest(BufferedReader reader) throws IOException {
        this.requestLine = new RequestLine(parseRequestLine(reader));
        this.headers = new Headers(parseHeaders(reader));
        this.requestBody = new RequestBody(parseRequestBody(reader));
    }

    private String[] parseRequestLine(BufferedReader reader) throws IOException {
        String requestHeader = reader.readLine();
        if (requestHeader == null) {
            throw new IllegalArgumentException("올바르지 않은 요청입니다.");
        }
        return requestHeader.split(REQUEST_DELIMITER);
    }

    private Map<String, List<String>> parseHeaders(BufferedReader reader) throws IOException {
        Map<String, List<String>> headerValues = new LinkedHashMap<>();
        String line = reader.readLine();
        while (!line.isBlank()) {
            String[] keyAndValue = line.split(HEADER_DELIMITER);
            putHeader(headerValues, keyAndValue);
            line = reader.readLine();
        }

        return headerValues;
    }

    private void putHeader(Map<String, List<String>> headers, String[] keyAndValue) {
        String headerName = keyAndValue[0];
        String headerValue = keyAndValue[1];
        if (!headers.containsKey(headerName)) {
            ArrayList<String> headerValues = new ArrayList<>();
            headerValues.add(headerValue.trim());
            headers.put(headerName.trim(), headerValues);
            return;
        }

        List<String> headerValues = headers.get(headerName);
        headerValues.add(headerValue);
    }

    private String parseRequestBody(BufferedReader reader) throws IOException {
        if (headers.hasNoContent()) {
            return null;
        }

        int contentLength = Integer.parseInt(headers.getHeaderValue(HttpHeaders.CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return URLDecoder.decode(new String(buffer), StandardCharsets.UTF_8);
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getParam(String key) {
        return requestBody.getParam(key);
    }

    public boolean isGetRequest() {
        return requestLine.isGetRequest();
    }

    public boolean isPostRequest() {
        return requestLine.isPostRequest();
    }

    public boolean hasQueryParams() {
        return requestLine.hasQueryParams();
    }

    public String getQueryParam(String param) {
        return requestLine.getQueryParam(param);
    }
}
