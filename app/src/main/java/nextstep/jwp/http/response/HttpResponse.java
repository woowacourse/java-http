package nextstep.jwp.http.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private static final String NOT_FOUND_URI = "/404.html";

    private StatusLine statusLine;
    private ResponseHeaders headers = new ResponseHeaders();
    private ResponseBody body = new ResponseBody();

    public void setStatusLine(Status status) {
        this.statusLine = new StatusLine(status);
    }

    public void setHeaders(String uri) {
        this.headers = new ResponseHeaders();
        headers.setContentType(ContentType.findByUri(uri));
    }

    public void setBody(String content) {
        this.body = new ResponseBody(content);
    }

    public void setBodyByUri(String uri) {
        this.body = new ResponseBody(uri);
        setHeaders(uri);
    }

    public void forward(String uri) {
        try {
            URL resource = getClass().getClassLoader().getResource("static" + uri);
            if (resource == null) {
                log.info("Resource is not found!");
                response404();
                return;
            }
            final Path resourcePath = new File(resource.getPath()).toPath();
            final List<String> requestBody = Files.readAllLines(resourcePath);
            String content = String.join("\r\n", requestBody) + "\r\n";

            this.setStatusLine(Status.OK);
            this.headers.setContentType(ContentType.findByUri(uri));
            this.headers.setContentLength(content.length());
            this.body = new ResponseBody(content);
        } catch (Exception exception) {
            log.error("Exception set response body", exception);
        }
    }

    private void response404() {
        try {
            URL resource = getClass().getClassLoader().getResource("static" + NOT_FOUND_URI);
            assert resource != null;
            final Path resourcePath = new File(resource.getPath()).toPath();
            final List<String> requestBody = Files.readAllLines(resourcePath);
            String content = String.join("\r\n", requestBody);
            this.setStatusLine(Status.NOT_FOUND);
            this.headers.setContentType(ContentType.HTML);
            this.headers.setContentLength(content.length());
            this.body = new ResponseBody(content);
        } catch (Exception exception) {
            log.error("Exception set response body", exception);
        }
    }

    public void redirect(String redirectUrl) {
        this.statusLine = new StatusLine(Status.FOUND);
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
        return String.join("\r\n", statusLine.toString(), headers.toString(), "", body.toString());
    }
}
