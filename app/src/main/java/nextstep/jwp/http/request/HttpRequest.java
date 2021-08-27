package nextstep.jwp.http.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.Map;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private RequestLine requestLine;
    private RequestHeaders headers;
    private RequestBody body;

    public HttpRequest(BufferedReader reader) {
        this.requestLine = new RequestLine(reader);
        this.headers = new RequestHeaders(reader);
    }

    public Map<String, String> getParams() {
        return requestLine.getParams();
    }

    public String getUri() {
        return requestLine.getUri();
    }
}
