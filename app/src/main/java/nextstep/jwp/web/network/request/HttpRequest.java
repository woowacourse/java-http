package nextstep.jwp.web.network.request;

import nextstep.jwp.web.network.URI;
import nextstep.jwp.web.network.response.HttpHeaders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(InputStream inputStream) {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        this.requestLine = RequestLine.of(bufferedReader);
        this.headers = HttpHeaders.of(bufferedReader);
        this.body = HttpBody.of(bufferedReader, contentLength());
    }

    private int contentLength() {
        final String contentLengthAsString = this.headers.get("Content-Length");
        if (contentLengthAsString.equals("")) {
            return 0;
        }
        return Integer.parseInt(this.headers.get("Content-Length"));
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public URI getURI() {
        return requestLine.getURI();
    }

    public Map<String, String> bodyAsMap() {
        return body.asMap();
    }

    public String getPath() {
        return getURI().getPath();
    }
}
