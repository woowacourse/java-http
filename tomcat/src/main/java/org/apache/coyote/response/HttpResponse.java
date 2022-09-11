package org.apache.coyote.response;

import java.io.IOException;
import org.apache.coyote.common.StaticFile;
import org.apache.coyote.common.StatusCode;
import org.apache.coyote.session.Session;

public class HttpResponse {

    private StatusLine statusLine;
    private ResponseHeader header;
    private ResponseBody body;

    public HttpResponse() {
    }

    public void ok(final String path) throws IOException {
        updateByStatusCodeAndPath(StatusCode.OK, path);
    }

    public void redirect(final String path) throws IOException {
        updateByStatusCodeAndPath(StatusCode.FOUND, path);
    }

    public void unauthorized(final String path) throws IOException {
        updateByStatusCodeAndPath(StatusCode.UNAUTHORIZED, path);
    }

    private void updateByStatusCodeAndPath(final StatusCode statusCode, final String path) throws IOException {
        this.statusLine = StatusLine.from(statusCode);
        final String content = StaticFile.load(path);
        this.header = ResponseHeader.of(StaticFile.parseContentType(path), String.valueOf(content.getBytes().length));
        this.body = new ResponseBody(content);
    }

    public void setSession(final Session session) {
        header.setSession(session.getId());
    }

    @Override
    public String toString() {
        return new StringBuilder(statusLine.toString())
                .append(header.toString())
                .append("\r\n")
                .append(body.getContent())
                .toString();
    }
}
