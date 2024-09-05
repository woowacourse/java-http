package org.apache.coyote.http11;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final Body body;

    public HttpRequest(InputReader inputReader) {
        this.requestLine = new RequestLine(inputReader.readRequestLine());
        this.headers = new Headers(inputReader.readHeaders());
        this.body = new Body(inputReader.readBody());
    }

    public boolean isCss() {
        String uri = getUri();
        int index = uri.lastIndexOf(".");

        return uri.substring(index + 1).equals("css");
    }

    public String getUri() {
        return requestLine.getUri();
    }
}
