package nextstep.jwp.http.response;

import com.google.common.base.Strings;
import nextstep.jwp.http.response.type.ContentType;
import nextstep.jwp.http.response.type.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private static final String NOT_FOUND_URI = "/404.html";

    private StatusLine statusLine = new StatusLine();
    private ResponseHeaders headers = new ResponseHeaders();
    private ResponseBody body = new ResponseBody();

    public void setStatusLine(StatusCode statusCode) {
        this.statusLine = new StatusLine(statusCode);
    }

    public void forward(String uri) {
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
            log.error("Exception stream", exception);
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
            log.error("Exception stream", exception);
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

    public void redirect(String redirectUrl) {
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

    @Override
    public String toString() {
        if (Strings.isNullOrEmpty(body.toString())) {
            return String.join("\r\n", statusLine.toString(), headers.toString());
        }
        return String.join("\r\n", statusLine.toString(), headers.toString(), body.toString());
    }
}
