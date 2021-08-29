package nextstep.jwp.http.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private final ResponseHeader responseHeader;
    private StatusLine statusLine;
    private ResponseBody responseBody;

    public HttpResponse() {
        this.responseHeader = new ResponseHeader();
    }

    public void forward(String uri) throws IOException {
        try {
            URL resource = getClass().getClassLoader().getResource("static" + uri);
            String content = getContent(resource);
            if (content == null) {
                notFound();
                return;
            }

            setStatusLine(StatusCode.OK);
            this.responseHeader.setContentType(ContentType.findByUri(uri));
            this.responseHeader.setContentLength(content.getBytes(StandardCharsets.UTF_8).length);
            this.responseBody = new ResponseBody(content);
        } catch (IOException e) {
            log.error("stream exception");
        }
    }

    public void redirect(String redirectUrl) {
        setStatusLine(StatusCode.FOUND);
        this.responseHeader.setLocation(redirectUrl);
    }

    private String getContent(URL resource) throws IOException {
        if (resource == null) {
            return null;
        }
        Path path = new File(resource.getPath()).toPath();
        return Files.readString(path);
    }

    private void notFound() throws IOException {
        try {
            URL resource = getClass().getClassLoader().getResource("/404.html");
            String content = getContent(resource);

            this.setStatusLine(StatusCode.NOT_FOUND);
            this.responseHeader.setContentType(ContentType.HTML);
            this.responseBody = new ResponseBody(content);
        } catch (IOException e) {
            log.error("stream exception");
        }

    }

    public void message(String message) {
        setStatusLine(StatusCode.OK);
        responseHeader.setContentType(ContentType.HTML);
        responseHeader.setContentLength(message.length());
        responseBody = new ResponseBody(message);
    }

    public void setStatusLine(StatusCode statusCode) {
        this.statusLine = new StatusLine(statusCode);
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }

    @Override
    public String toString() {
        if (responseBody == null) {
            return String.join("\r\n", statusLine.toString(), responseHeader.toString());
        }
        return String.join("\r\n", statusLine.toString(), responseHeader.toString(),
            responseBody.toString());
    }
}
