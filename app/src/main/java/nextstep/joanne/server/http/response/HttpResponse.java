package nextstep.joanne.server.http.response;

import nextstep.joanne.server.http.Headers;
import nextstep.joanne.server.http.HttpStatus;

import java.util.Arrays;
import java.util.StringJoiner;

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
        StringJoiner stringJoiner = new StringJoiner(" ");
        stringJoiner.add(statusLine.getVersion());
        stringJoiner.add(statusLine.getStatusCode());
        stringJoiner.add(statusLine.getStatusMessage());
        return stringJoiner.toString();
    }

    public String getHeaders() {
        return headers.getHeaders();
    }

    public String getStatus() {
        return statusLine.getStatusCode();
    }

    public String getBody() {
        return getStatusLine() + "\n" +
                getHeaders() + "\n" +
                body.getBody();
    }
}
