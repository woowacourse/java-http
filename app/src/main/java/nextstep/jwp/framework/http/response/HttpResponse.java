package nextstep.jwp.framework.http.response;

import static nextstep.jwp.framework.http.request.HttpRequest.LINE_DELIMITER;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.framework.http.common.HttpBody;
import nextstep.jwp.framework.http.common.HttpHeaders;
import nextstep.jwp.framework.http.common.HttpStatus;
import nextstep.jwp.framework.http.common.ProtocolVersion;
import nextstep.jwp.framework.http.request.HttpRequestLine;

public class HttpResponse {

    private static final String SPACE = " ";

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
        if (body.hasNotBody()) {
            getResponseAsBytes();
        }
        return getResponseAsBytesWithBody();
    }

    private byte[] getResponseAsBytes() {
        return String.join(LINE_DELIMITER,
            statusLine(),
            headers.toString(),
            headers.cookieToString()
        ).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getResponseAsBytesWithBody() throws IOException {
        return String.join(LINE_DELIMITER,
            statusLine(),
            headers.toString(),
            headers.cookieToString(),
            "",
            body()
        ).getBytes(StandardCharsets.UTF_8);
    }

    private String statusLine() {
        return protocolVersion.getProtocolVersion() + SPACE + status.value() + SPACE + status.getReasonPhrase() + SPACE;
    }

    public String body() throws IOException {
        final Path path = new File(resourceURL.getPath()).toPath();
        return Files.readString(path);
    }
}
