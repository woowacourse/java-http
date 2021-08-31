package nextstep.jwp.framework.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class HttpResponse {

    private static final String NEW_LINE = System.lineSeparator();

    private ProtocolVersion protocolVersion;
    private HttpStatus status;
    private HttpHeaders headers;
    private HttpBody body;
    private URL resourceURL;

    public void create(HttpRequestLine requestLine, HttpHeaders headers, HttpBody body, HttpStatus status) {
        this.protocolVersion = requestLine.getProtocolVersion();
        this.headers = headers;
        this.body = body;
        this.status = status;
        this.resourceURL = requestLine.url(status);
    }

    public byte[] getBytes() throws IOException {
        if (Objects.isNull(body())) {
            return getResponseAsBytesWithEmptyBody();
        }
        return getResponseAsBytesWithBody();
    }

    private byte[] getResponseAsBytesWithEmptyBody() {
        return String.join(NEW_LINE,
            statusLine(),
            headers.toString())
            .getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getResponseAsBytesWithBody() throws IOException {
        return String.join(NEW_LINE,
            statusLine(),
            "Host: localhost:8080",
            "Accept: text/css,*/*;q=0.1",
            "Connection: keep-alive",
            "Content-Length: " + body().getBytes().length + " ",
            "",
            body())
            .getBytes(StandardCharsets.UTF_8);
    }

    private String statusLine() {
        return protocolVersion.getProtocolVersion() + " " + status.value() + " " + status.getReasonPhrase() + " ";
    }

    public String body() throws IOException {
        final Path path = new File(resourceURL.getPath()).toPath();
        return Files.readString(path);
    }
}
