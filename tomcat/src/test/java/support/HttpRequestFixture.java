package support;

import org.apache.coyote.http11.request.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpRequestFixture {

    public static HttpRequest get(String path) throws IOException {
        return request("GET", path);
    }

    public static HttpRequest request(String method, String path) throws IOException {
        String raw = String.join("\r\n",
                method + " " + path + " HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        return HttpRequest.from(new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8)));
    }

}
