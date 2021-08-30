package nextstep.jwp.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;

// 응답 데이터의 상태에 따라 적절한 HTTP 헤더를 처리하는 역할
// HTML, CSS, JS를 읽어 반환하는 부분과 302 코드를 처리하는 부분 담당
public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private final OutputStream outputStream;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void forward(String uri) {
        try {
            byte[] body = Files.readAllBytes(getResources(uri).toPath());
            ContentType contentType = findContentType(uri);
            String response = responseOk(contentType, body);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void redirect(String location) {
        try {
            String response = responseFound(location);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private ContentType findContentType(String uri) {
        if (uri.endsWith(".css")) {
            return ContentType.CSS;
        }

        if (uri.endsWith(".js")) {
            return ContentType.JS;
        }

        if (uri.startsWith("/assets/img")) {
            return ContentType.IMAGE;
        }

        return ContentType.HTML;
    }

    private String responseOk(ContentType contentType, byte[] body) {
        return String.join("\r\n",
                "HTTP/1.1 " + HttpStatus.OK.getStatus(),
                "Content-Type: " + contentType.getType(),
                "Content-Length: " + body.length + " ",
                "",
                new String(body));
    }

    private String responseFound(String location) {
        return String.join("\r\n",
                "HTTP/1.1 " + HttpStatus.FOUND.getStatus(),
                "Location: " + location,
                "");
    }

    private File getResources(String fileName) {
        final URL resource = getClass().getClassLoader().getResource("static" + fileName);
        if (resource != null) {
            return new File(resource.getPath());
        }
        return getResources("/404.html");
    }
}
