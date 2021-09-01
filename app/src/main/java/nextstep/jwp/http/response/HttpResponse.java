package nextstep.jwp.http.response;

import nextstep.jwp.http.response.type.ContentType;
import nextstep.jwp.http.response.type.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private static final String NOT_FOUND_URI = "/404.html";

    private final ResponseHeaders headers;
    private StatusLine statusLine;
    private ResponseBody body;

    public HttpResponse() {
        this.headers = new ResponseHeaders();
    }

    public void setStatusLine(StatusCode statusCode) {
        this.statusLine = new StatusLine(statusCode);
    }

    public void responseOk(String uri) {
        try {
            URL resource = findResource(uri);
            String content = readContent(resource);
            if (content == null) {
                responseNotFound();
                return;
            }

            setStatusLine(StatusCode.OK);
            this.headers.setContentType(ContentType.findByUri(uri));
            this.headers.setContentLength(content.getBytes(StandardCharsets.UTF_8).length);
            this.body = new ResponseBody(content);
        } catch (IOException exception) {
            log.error("Exception input stream", exception);
        }
    }

    public void responseNotFound() {
        try {
            URL resource = findResource(NOT_FOUND_URI);
            String content = readContent(resource);
            assert content != null;

            this.setStatusLine(StatusCode.NOT_FOUND);
            this.headers.setContentType(ContentType.HTML);
            this.headers.setContentLength(content.length());
            this.body = new ResponseBody(content);
        } catch (IOException exception) {
            log.error("Exception input stream", exception);
        }
    }

    public void responseMessage(String message) {
        this.headers.setContentType(ContentType.HTML);
        this.headers.setContentLength(message.length());
        this.body = new ResponseBody(message);
    }

    private URL findResource(String uri) {
        return getClass().getClassLoader().getResource("static" + uri);
    }

    private String readContent(URL resource) throws IOException {
        if (resource == null) {
            log.debug("Resource is not found!");
            return null;
        }
        final Path resourcePath = new File(resource.getPath()).toPath();
        byte[] bytes = Files.readAllBytes(resourcePath);
        return new String(bytes);
    }

    public void responseRedirect(String redirectUrl) {
        setStatusLine(StatusCode.FOUND);
        this.headers.setLocation(redirectUrl);
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public ResponseHeaders getHeaders() {
        return headers;
    }

    public ResponseBody getBody() {
        return body;
    }

    public void write(OutputStream outputStream) {
        try {
            if (Objects.isNull(body)) {
                writeWithoutBody(outputStream);
                return;
            }
            writeWithBody(outputStream);
        } catch (IOException exception) {
            log.error("Exception output stream", exception);
        }
    }

    private void writeWithBody(OutputStream outputStream) throws IOException {
        outputStream.write(statusLine.getByte());
        outputStream.write(headers.getByte());
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.write(body.getByte());
        outputStream.flush();
    }

    private void writeWithoutBody(OutputStream outputStream) throws IOException {
        outputStream.write(statusLine.getByte());
        outputStream.write(headers.getByte());
        outputStream.flush();
    }

    public void setCookie(String cookie) {
        this.headers.setCookie(cookie);
    }
}
