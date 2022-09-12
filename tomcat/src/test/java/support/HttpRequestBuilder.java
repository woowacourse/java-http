package support;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;

public class HttpRequestBuilder {

    private RequestLine requestLine;
    private RequestHeader requestHeader;
    private RequestBody requestBody;

    public HttpRequestBuilder() {}

    private HttpRequestBuilder(RequestLine requestLine, RequestHeader requestHeader,
                              RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    public HttpRequestBuilder requestLine(String requestLine) {
        return new HttpRequestBuilder(RequestLine.from(requestLine), requestHeader, requestBody);
    }

    public HttpRequestBuilder requestHeader(Map<String, String> requestHeader) {
        return new HttpRequestBuilder(requestLine, RequestHeader.from(requestHeader), requestBody);
    }

    public HttpRequestBuilder requestBody(String requestBody) {
        return new HttpRequestBuilder(requestLine, requestHeader, RequestBody.from(requestBody));
    }

    public HttpRequest build() {
        return new HttpRequest(requestLine, requestHeader, requestBody);
    }
}
