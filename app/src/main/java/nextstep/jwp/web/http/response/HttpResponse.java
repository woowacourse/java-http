package nextstep.jwp.web.http.response;

import nextstep.jwp.web.http.Headers;

public class HttpResponse {

    private StatusLine statusLine;
    private Headers headers;
    private ResponseBody body;

    public HttpResponse() {
        this.statusLine = new StatusLine();
        this.headers = new Headers();
        this.body = new ResponseBody();
    }

    public void setStatusLine(StatusCode statusCode) {
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
