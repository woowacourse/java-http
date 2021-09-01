package nextstep.jwp.httpmessage.stub;

import nextstep.jwp.httpmessage.ContentType;
import nextstep.jwp.httpmessage.HttpMessageReader;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

@DisplayName("Get요청일 때 HttpMessageReader Stub")
public class HttpMessageReaderGetStub extends HttpMessageReader {

    private final String requestUri;
    private final ContentType contentType;

    public HttpMessageReaderGetStub(String requestUri, ContentType contentType) {
        super(new ByteArrayInputStream(new byte[]{}));
        this.requestUri = requestUri;
        this.contentType = contentType;
    }

    @Override
    public String getStartLine() {
        return "GET " + requestUri + " HTTP/1.1";
    }

    @Override
    public Map<String, String> getHeaders() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Content-Type", contentType.getValue());
        headers.put("Connection", "keep-alive");
        return headers;
    }
}
