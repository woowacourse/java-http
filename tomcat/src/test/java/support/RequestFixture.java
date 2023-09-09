package support;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.HttpRequestDecoder;

public enum RequestFixture {

    GET("GET "),
    ;

    private static final HttpRequestDecoder REQUEST_DECODER = new HttpRequestDecoder();

    public final String method;

    RequestFixture(String method) {
        this.method = method;
    }

    public HttpRequest buildRequestToResource(String resource) {
        String requestLine = method + resource + " HTTP/1.1\r\n";
        return REQUEST_DECODER.decode(
            new BufferedReader(new InputStreamReader(new ByteArrayInputStream(requestLine.getBytes(StandardCharsets.UTF_8))
        )));
    }
}
