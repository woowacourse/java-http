package nextstep.jwp.httpmessage.stub;

import nextstep.jwp.httpmessage.ContentType;
import nextstep.jwp.httpmessage.HttpMessageReader;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

@DisplayName("Post요청일 때 HttpMessageReader Stub")
public class HttpMessageReaderPostStub extends HttpMessageReader {

    private final String requestUri;
    private final ContentType contentType;

    public HttpMessageReaderPostStub(String requestUri, ContentType contentType) {
        super(new ByteArrayInputStream(new byte[]{}));
        this.requestUri = requestUri;
        this.contentType = contentType;
    }

    @Override
    public String getStartLine() {
        return "POST " + requestUri + " HTTP/1.1";
    }

    @Override
    public Map<String, String> getHeaders() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Content-Type", contentType.getContentType());
        headers.put("Connection", "keep-alive");
        headers.put("Content-Length", "0");
        headers.put("Accept", "/*/");
        return headers;
    }

    @Override
    public Map<String, String> getParameters() {
        return Map.ofEntries(
                Map.entry("account", "gumgum"),
                Map.entry("password", "password2"),
                Map.entry("email", "ggump%40woowahan.com")
        );
    }
}
