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

    private final ProtocolVersion protocolVersion;
    private final HttpStatus status;
    private final HttpHeaders headers;
    private final URL resourceURL;

    public HttpResponse(final HttpRequestLine httpRequestLine, final HttpStatus status, final HttpHeaders headers) {
        this.protocolVersion = httpRequestLine.getProtocolVersion();
        this.status = status;
        this.headers = headers;
        this.resourceURL = httpRequestLine.getURL();
    }

//    public String getResponse() throws IOException {
//        try {
//
//
//            return createResponse(httpStatus, content);
//        } catch (ArrayIndexOutOfBoundsException e) {
//            final Path path = new File(HttpPath.notFound().getPath()).toPath();
//            final String content = Files.readString(path);
//
//            return createResponse(HttpStatus.NOT_FOUND, content);
//        }
//    }

    private String createResponse(HttpStatus httpStatus, String content) {
        final String requestLine = protocolVersion.getProtocolVersion() + " " + httpStatus.value() + " " + httpStatus.getReasonPhrase() + " ";
        return String.join("\r\n",
            requestLine,
            headers.getHeaders(), // + content.getBytes().length + " ",
            content);
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
            headers.getHeaders())
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
