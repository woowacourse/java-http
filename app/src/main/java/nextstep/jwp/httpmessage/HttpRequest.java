package nextstep.jwp.httpmessage;

public class HttpRequest {

    private final String requestLine;

    public HttpRequest(HttpMessageReader bufferedReader) {
        this(bufferedReader.getStartLine());
    }

    public HttpRequest(String requestLine) {
        this.requestLine = requestLine;
    }

    public String getRequestLine() {
        return requestLine;
    }
}
