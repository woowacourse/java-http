package nextstep.jwp.http.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private RequestLine requestLine;
    private RequestHeaders headers;
    private RequestBody body;

    public HttpRequest(BufferedReader reader) {
        this.requestLine = new RequestLine(reader);
        this.headers = new RequestHeaders(reader);
    }

    public String getParams(String key) {
        return requestLine.getQueryParams(key);
    }

    public String getUri() {
        return requestLine.getPath();
    }

    public HttpMethod getMethod(){
        return requestLine.getMethod();
    }

    public boolean isGet() {
        return this.requestLine.isGet();
    }

    public boolean isPost() {
        return this.requestLine.isPost();
    }
}
