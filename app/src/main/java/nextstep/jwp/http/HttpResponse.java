package nextstep.jwp.http;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {
    private final OutputStream outputStream;
    private HttpStatus status;
    private String body;
    private String path;
    private String redirectUrl;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void forward() throws IOException {
        updateOutputStream(createResponse());
    }

    public void redirect() throws IOException {
        updateOutputStream(createRedirectResponse());
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    private String createResponse() {
        String contentType = ContentTypeMapper.extractContentType(path);
        return String.join("\r\n",
                "HTTP/1.1 " + status.number + " " + status.name + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    private String createRedirectResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + status.number + " " + status.name + " ",
                "Location: http://localhost:8080" + redirectUrl);
    }

    private void updateOutputStream(String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
