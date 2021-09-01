package nextstep.jwp.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

// 응답 데이터의 상태에 따라 적절한 HTTP 헤더를 처리하는 역할
// HTML, CSS, JS를 읽어 반환하는 부분과 302 코드를 처리하는 부분 담당
public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private final OutputStream outputStream;
    private final Map<String, String> headers = new HashMap<>();

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void forward(String uri) {
        try {
            byte[] body = Files.readAllBytes(getResources(uri).toPath());
            ContentType contentType = ContentType.findContentType(uri);
            addHeader("Content-Type", contentType.getType());
            addHeader("Content-Length", String.valueOf(body.length));
            responseHeader(HttpStatus.OK);
            responseBody(body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void redirect(String location) {
        try {
            addHeader("Location", location);
            responseHeader(HttpStatus.FOUND);
            responseBody("".getBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    private void responseHeader(HttpStatus status) throws IOException {
        outputStream.write(makeStatusHeader(status).getBytes());
        processHeaders();
        outputStream.write("\r\n".getBytes());
    }

    private void responseBody(byte[] body) throws IOException {
        outputStream.write(body);
        outputStream.write("\r\n".getBytes());
        outputStream.flush();
    }

    private void processHeaders() throws IOException {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String eachHeader = entry.getKey() + ": " + entry.getValue() + " \r\n";
            outputStream.write(eachHeader.getBytes());
        }
    }

    private String makeStatusHeader(HttpStatus status) {
        return String.join("\r\n",
                "HTTP/1.1 " + status.getStatus() + " \r\n");
    }

    private File getResources(String fileName) {
        final URL resource = getClass().getClassLoader().getResource("static" + fileName);
        if (resource != null) {
            return new File(resource.getPath());
        }
        return getResources("/404.html");
    }
}
