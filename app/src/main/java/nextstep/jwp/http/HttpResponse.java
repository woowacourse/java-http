package nextstep.jwp.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final OutputStream outputStream;
    private final ResponseHeaders responseHeaders;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.responseHeaders = new ResponseHeaders();
    }

    public void addHeaders(String key, String value) {
        responseHeaders.addHeaders(key, value);
    }

    public void writeStatusLine(HttpStatus status) throws IOException {
        final String line = String.format(
                "HTTP/1.1 %d %s \r\n", status.getCode(), status.getMessage());

        log.debug(line);
        outputStream.write(line.getBytes());
    }

    public void writeHeaders() throws IOException {
        String headers = responseHeaders.getHeaders()
                .keySet().stream()
                .map(key -> String.format("%s: %s ", key, responseHeaders.getHeaders().get(key)))
                .collect(Collectors.joining("\r\n"));

        outputStream.write(headers.getBytes());
        outputStream.write("\r\n".getBytes());
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
