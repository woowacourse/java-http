package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String HEADER_KEY_VALUE_SEPARATOR = ":";
    private static final String QUERY_STRING_SEPARATOR = "&";
    private static final String QUERY_STRING_KEY_VALUE_SEPARATOR = "=";

    private final RequestLine requestLine;
    private final Map<HttpHeader, String> headers;
    private final String body;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.requestLine = new RequestLine(bufferedReader.readLine());
        this.headers = parseHeaders(bufferedReader);
        this.body = parseBody(bufferedReader);
    }

    private Map<HttpHeader, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        Map<HttpHeader, String> headers = new HashMap<>();
        while (true) {
            String header = bufferedReader.readLine();
            if (header.isBlank()) {
                break;
            }
            String[] headerKeyValue = header.split(HEADER_KEY_VALUE_SEPARATOR);
            headers.put(HttpHeader.findByName(headerKeyValue[0].trim()), headerKeyValue[1].trim());
        }
        return headers;
    }

    private String parseBody(BufferedReader bufferedReader) throws IOException {
        String rawContentLength = headers.get(HttpHeader.CONTENT_LENGTH);
        if (rawContentLength == null) {
            return null;
        }
        int contentLength = Integer.parseInt(rawContentLength);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    public HttpCookie getCookie() {
        return new HttpCookie(headers.get(HttpHeader.COOKIE));
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getBodyQueryString() {
        Map<String, String> queryStrings = new HashMap<>();
        for (String queryString : body.split(QUERY_STRING_SEPARATOR)) {
            String[] queryStringKeyValue = queryString.split(QUERY_STRING_KEY_VALUE_SEPARATOR);
            if (queryStringKeyValue.length == 2) {
                queryStrings.put(queryStringKeyValue[0], queryStringKeyValue[1]);
            }
        }

        return queryStrings;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }
}
