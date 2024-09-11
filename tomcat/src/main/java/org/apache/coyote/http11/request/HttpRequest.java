package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {
    private static final String METHOD = "Method";
    private static final String URI = "URI";
    private static final String VERSION = "Version";
    private static final String DEFAULT = "";
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int REQUEST_LINE_SIZE = 3;
    private static final int METHOD_POSITION = 0;
    private static final int URI_POSITION = 1;
    private static final int VERSION_POSITION = 2;
    private static final String HEADER_DELIMITER = ":";
    private static final int HEADER_LIMIT = 2;
    private static final int HEADER_KEY_POSITION = 0;
    private static final int HEADER_VALUE_POSITION = 1;

    private final HttpMethod method;
    private final String uri;
    private final String version;
    private final HttpHeaders headers;
    private final RequestBody body;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestLine = extractRequestLine(bufferedReader);
        this.method = HttpMethod.of(requestLine.getOrDefault(METHOD, DEFAULT));
        this.uri = requestLine.getOrDefault(URI, DEFAULT);
        this.version = requestLine.getOrDefault(VERSION, DEFAULT);
        this.headers = extractHeaders(bufferedReader);
        this.body = extractRequestBody(bufferedReader, headers);
    }

    private Map<String, String> extractRequestLine(BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestMap = new HashMap<>();

        String line = bufferedReader.readLine();
        if (Objects.nonNull(line)) {
            String[] requestLine = line.split(REQUEST_LINE_DELIMITER);
            if (requestLine.length >= REQUEST_LINE_SIZE) {
                requestMap.put(METHOD, requestLine[METHOD_POSITION]);
                requestMap.put(URI, requestLine[URI_POSITION]);
                requestMap.put(VERSION, requestLine[VERSION_POSITION]);
            }
        }

        return requestMap;
    }

    private HttpHeaders extractHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] headerField = line.split(HEADER_DELIMITER, HEADER_LIMIT);
            if (headerField.length == HEADER_LIMIT) {
                headers.put(headerField[HEADER_KEY_POSITION].trim(), headerField[HEADER_VALUE_POSITION].trim());
            }
        }

        return new HttpHeaders(headers);
    }

    public RequestBody extractRequestBody(BufferedReader bufferedReader, HttpHeaders headers) throws IOException {
        StringBuilder body = new StringBuilder();

        int contentLength;
        if (headers.haveContentLength() && (contentLength = headers.getContentLength()) > 0) {
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            body.append(buffer);
        }

        return new RequestBody(body.toString());
    }

    public HttpCookie getCookie() {
        return new HttpCookie(headers.getCookie());
    }

    public String getURI() {
        return uri;
    }

    public HttpMethod getHttpMethod() {
        return method;
    }

    public RequestBody getBody() {
        return body;
    }
}
