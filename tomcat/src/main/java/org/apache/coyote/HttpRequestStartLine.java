package org.apache.coyote;

public class HttpRequestStartLine {

    private final HttpMethod httpMethod;
    private final String requestURI;

    public HttpRequestStartLine(String line) {
        final String[] startLine = line.split(" ");
        if (startLine.length != 3) {
            throw new IllegalArgumentException("HttpRequest의 startLine 형식이 잘못되었습니다.");
        }
        String httpMethodName = startLine[0];
        this.httpMethod = HttpMethod.findByName(httpMethodName);
        this.requestURI = startLine[1];
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestURI() {
        return requestURI;
    }
}
