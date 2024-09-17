package support;


import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.RequestHeaders;
import org.apache.coyote.http11.RequestLine;

public class HttpRequestBuilder {

    private RequestLine requestLine;
    private List<String> rawHeaders = new ArrayList<>();
    private String requestBody = "";

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    public HttpRequestBuilder setRequest(String method, String url) {
        this.requestLine = new RequestLine("%s %s HTTP/1.1".formatted(method.toUpperCase(), url));
        return this;
    }

    public HttpRequestBuilder addRequestHeader(String headerName, String value) {
        rawHeaders.add("%s: %s".formatted(headerName, value));
        return this;
    }

    public HttpRequestBuilder setRequestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public HttpRequest build() {
        return new HttpRequest(requestLine, new RequestHeaders(rawHeaders), requestBody);
    }
}
