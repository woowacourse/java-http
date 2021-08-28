package nextstep.jwp.framework.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DBNotFoundException;

public class HttpResponse {

    private static final String NEW_LINE = System.lineSeparator();

    private final ProtocolVersion protocolVersion;
    private final HttpStatus status;
    private final HttpHeaders headers;
    private final URL resourceURL;

    public HttpResponse(final HttpRequestLine httpRequestLine, final HttpHeaders headers) {
        this.protocolVersion = httpRequestLine.getProtocolVersion();
        this.status = login(httpRequestLine.getPath());
        this.headers = headers;
        this.resourceURL = httpRequestLine.url(status);
    }

    private HttpStatus login(HttpPath path) {
        if (path.hasNotQueryParams()) {
            return HttpStatus.OK;
        }

        try {
            final Map<String, String> queryParams = path.queryParams();
            final String account = queryParams.get("account");
            InMemoryUserRepository.findByAccount(account).orElseThrow(DBNotFoundException::new);
            return HttpStatus.FOUND;
        } catch (DBNotFoundException ignored) {
            return HttpStatus.UNAUTHORIZED;
        }
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
