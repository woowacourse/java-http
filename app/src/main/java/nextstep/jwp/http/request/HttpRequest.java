package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class HttpRequest {

    private RequestStartLine requestStartLine;
    private RequestHeaders requestHeaders;
    private String body;

    public HttpRequest(RequestStartLine requestStartLine, RequestHeaders requestHeaders,
            String body) {
        this.requestStartLine = requestStartLine;
        this.requestHeaders = requestHeaders;
        this.body = body;
    }

    public static HttpRequest create(BufferedReader bufferedReader) {

        try {
            RequestStartLine requestStartLine = RequestStartLine.create(bufferedReader);
            RequestHeaders requestHeaders = new RequestHeaders(bufferedReader);
            String body = "";
            if (requestHeaders.isExistContentLength()) {
                body = readBody(bufferedReader, requestHeaders.contentLength());
            }

            return new HttpRequest(requestStartLine, requestHeaders, body);
        } catch (IOException e) {
            throw new RuntimeException(e.getCause() + ": " + e.getMessage());
        }
    }

    private static String readBody(BufferedReader bufferedReader, int contentLength)
            throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public Method getMethod() {
        return requestStartLine.getMethod();
    }

    public String getPath() {
        return requestStartLine.getPath();
    }

    public String getAttribute(String name) {
        return requestStartLine.getAttribute(name);
    }

    public String getProtocolVersion() {
        return requestStartLine.getVersionOfProtocol();
    }

    public List<String> getHeader(String header) {
        return requestHeaders.getHeader(header);
    }

    public String getBody() {
        return body;
    }

    public String getCookie(String name) {
        return requestHeaders.getCookie(name);
    }

}
