package nextstep.jwp.http;

import java.io.IOException;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final OutputStream outputStream;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeStatusLine(HttpStatus status) throws IOException {
        final String line = String.format(
                "HTTP/1.1 %d %s \r\n", status.getCode(), status.getMessage());

        log.debug(line);
        outputStream.write(line.getBytes());
    }

    public void writeHeaders(String content, ContentType contentType) throws IOException {
        final String type = String.format("Content-Type: %s \r\n", contentType.getContentType());
        final String length = String.format("Content-Length: %d \r\n", content.getBytes().length);

        log.debug(type);
        log.debug(length);
        outputStream.write(type.getBytes());
        outputStream.write(length.getBytes());
        outputStream.write("\r\n".getBytes());
    }

    public void writeBody(String body) throws IOException {
        outputStream.write(body.getBytes());
        outputStream.flush();
    }

    public void writeRedirect(String path) throws IOException {
        final String location = String.format("Location: %s", path);
        outputStream.write(location.getBytes());
        outputStream.flush();
    }
}
