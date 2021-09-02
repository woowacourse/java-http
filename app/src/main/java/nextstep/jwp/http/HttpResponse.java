package nextstep.jwp.http;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class HttpResponse {
    private HttpStatus status;
    private String body;
    private String path;

    public HttpResponse(HttpStatus status, String body, String path) throws IOException {
        this.status = status;
        this.body = createStaticFileResponseBody(body);
        this.path = path;
    }

    public String createResponse() {
        String contentType = ContentTypeMapper.extractContentType(path);
        return String.join("\r\n",
                "HTTP/1.1 " + status.number + " " + status.name + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    public String createRedirectResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + status.number + " " + status.name + " ",
                "Location: http://localhost:8080" + path);
    }

    private String createStaticFileResponseBody(String render) throws IOException {
        String filePath = "static" + render;
        final URL url = getClass().getClassLoader().getResource(filePath);
        File file = new File(Objects.requireNonNull(url).getFile());
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes);
    }
}
