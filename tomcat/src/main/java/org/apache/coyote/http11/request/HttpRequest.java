package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    private HttpRequest(RequestLine requestLine, RequestHeaders headers, RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        String firstLine = br.readLine();

        RequestLine requestLine = RequestLine.from(firstLine);
        RequestHeaders headers = getHeaders(br);
        RequestBody requestBody = getRequestBody(br, headers);

        return new HttpRequest(requestLine, headers, requestBody);


    }

    public static RequestHeaders getHeaders(BufferedReader br) throws IOException {
        RequestHeaders headers = new RequestHeaders();
        String line = br.readLine();
        while (!"".equals(line)) {
            String[] splitHeaders = line.split(": ");
            headers.add(splitHeaders[0], splitHeaders[1]);
            line = br.readLine();
        }
        return headers;
    }

    public static RequestBody getRequestBody(BufferedReader br, RequestHeaders headers) throws IOException {
        if (!headers.containValue("Content-Length")) {
            return null;
        }
        int contentLength = Integer.parseInt(headers.getValue("Content-Length"));
        char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);

        return RequestBody.from(new String(buffer));
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean containsQuery() {
        return requestLine.containsQuery();
    }

    public boolean containsQuery(final String key) {
        return requestLine.containsQuery(key);
    }

    public String getQueryParameter(final String queryKey) {
        return requestLine.getQueryParameter(queryKey);
    }

    public boolean containsBody(final String key) {
        return body.containValue(key);
    }

    public String getBody(final String key) {
        return body.get(key);
    }
}
