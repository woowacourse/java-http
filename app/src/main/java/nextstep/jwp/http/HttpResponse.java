package nextstep.jwp.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponse.class);

    private static final String DEFAULT_MESSAGE = "Hello world!";
    private static final String DEFAULT_RESOURCE_PATH = "static";

    private final OutputStream outputStream;
    private final HttpHeaders headers = new HttpHeaders(new LinkedHashMap<>());

    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void transfer(final String url) throws IOException {
        LOGGER.debug("url : {}", url);
        setContentType(url);

        String responseBody;
        if (url.equals("/")) {
            responseBody = DEFAULT_MESSAGE;
        } else if (url.equals("/login")) {
            responseBody = getBodyByUrl(DEFAULT_RESOURCE_PATH + url + ".html");
        } else {
            responseBody = getBodyByUrl(DEFAULT_RESOURCE_PATH + url);
        }
        headers.put("Content-Length: " + responseBody.getBytes().length);
        response200(responseBody);
    }

    private void setContentType(final String url) {
        if (url.endsWith(".html")) {
            headers.put("Content-Type: text/html;charset=utf-8");
        } else if (url.endsWith(".css")) {
            headers.put("Content-Type: text/css");
        } else if (url.endsWith(".js")) {
            headers.put("Content-Type: application/javascript");
        } else {
            headers.put("Content-Type: text/html;charset=utf-8");
        }
    }

    private String getBodyByUrl(final String url) throws IOException {
        String resourceUrl = Objects.requireNonNull(getClass().getClassLoader().getResource(url)).getPath();
        Path path = Paths.get(resourceUrl);
        return Files.readString(path);
    }

    private void response200(final String responseBody) throws IOException {
        String okResponse = "HTTP/1.1 200 OK\r\n";
        outputStream.write(okResponse.getBytes(StandardCharsets.UTF_8));
        responseHeaderBody(responseBody);
    }

    private void responseHeaderBody(final String responseBody) throws IOException {
        outputStream.write(headers.getAllHeaders().getBytes(StandardCharsets.UTF_8));
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.write(responseBody.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
