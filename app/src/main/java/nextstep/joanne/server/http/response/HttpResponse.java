package nextstep.joanne.server.http.response;

import nextstep.joanne.server.http.Headers;
import nextstep.joanne.server.http.HttpStatus;

import java.util.Objects;

public class HttpResponse {
    private StatusLine statusLine;
    private Headers headers;
    private Body body;

    public HttpResponse() {
        headers = new Headers();
    }

    public void addStatus(HttpStatus httpStatus) {
        this.statusLine = new StatusLine(httpStatus);
    }

    public void addHeaders(String key, String value) {
        this.headers.addHeader(key, value);
    }

    public void addBody(String uri) {
        this.body = new Body(uri);
        addContentLength();
    }

    private void addContentLength() {
        this.headers.addHeader("Content-Length", String.valueOf(body.getBody().getBytes().length));
    }

    public String getStatusLine() {
        return String.format("%s %s %s ", statusLine.getVersion(), statusLine.getStatusCode(), statusLine.getStatusMessage());
    }

    public String getHeaders() {
        return headers.getHeaders();
    }

    public String getStatus() {
        return statusLine.getStatusCode();
    }

    public String getBody() {
        String readBody = "";
        if (Objects.nonNull(body)) {
            readBody = body.getBody();
        }
        return String.format("%s\r\n%s\r\n%s", getStatusLine(), getHeaders(), readBody);
    }

    public void redirect(String uri) {
        addStatus(HttpStatus.FOUND);
        addHeaders("Location", uri);
    }
}
