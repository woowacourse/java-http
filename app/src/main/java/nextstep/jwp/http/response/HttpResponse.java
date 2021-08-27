package nextstep.jwp.http.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private StatusLine statusLine;
    private ResponseHeaders headers;
    private ResponseBody body;

    public HttpResponse() {
    }

    public void setStatusLine(Status status) {
        this.statusLine = new StatusLine(status);
    }

    public void setHeaders(String uri) {
        this.headers = new ResponseHeaders();
        headers.setContentType(ContentType.findByUri(uri));
    }

    public void setBodyByUri(String uri) {
        this.body = new ResponseBody(uri);
        setHeaders(uri);
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
