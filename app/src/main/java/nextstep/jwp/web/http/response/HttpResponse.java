package nextstep.jwp.web.http.response;

import nextstep.jwp.web.http.Headers;

public class HttpResponse {

    private final Headers headers;
    private StatusLine statusLine;
    private ResponseBody body;

    public HttpResponse() {
        this.headers = new Headers();
        this.statusLine = new StatusLine();
        this.body = new ResponseBody();
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusLine = new StatusLine(statusCode);
    }

    public void addHeader(String header, String value) {
        this.headers.add(header, value);
    }

    public void addBody(String body) {
        this.body = new ResponseBody(body);
    }

    public String asString() {
        return String.join("\r\n",
            statusLine.asString(),
            headers.asString(),
            body.getBody());
    }
}
