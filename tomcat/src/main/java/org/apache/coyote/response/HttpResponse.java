package org.apache.coyote.response;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.util.FileTypeChecker;

public class HttpResponse {

    private final HttpResponseStartLine startLine;
    private final HttpResponseHeaders headers;
    private final HttpResponseBody body;

    public HttpResponse(HttpResponseStartLine startLine, HttpResponseHeaders headers, HttpResponseBody body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse from(HttpRequest httpRequest) {
        HttpResponseStartLine startLine = HttpResponseStartLine.defaultStartLineFrom(httpRequest);
        return new HttpResponse(startLine, new HttpResponseHeaders(), new HttpResponseBody(null));
    }

    public void addContentType(String contentType) {
        if (FileTypeChecker.isHtml(contentType)) {
            headers.add("Content-Type", contentType + ";charset=utf-8");
            return;
        }
        headers.add("Content-Type", contentType);
    }

    public void addBody(String newBody) {
        this.body.update(newBody);
        if (this.body.hasBody()) {
            headers.add("Content-Length", String.valueOf(newBody.getBytes().length));
            return;
        }
        headers.add("Content-Length", "0");
    }

    public void updateHttpStatus(HttpStatus newHttpStatus) {
        startLine.updateStatus(newHttpStatus);
    }

    public boolean has5xxCode() {
        return startLine.has5xxCode();
    }

    @Override
    public String toString() {
        if (body == null) {
            return String.join(
                    "\r\n",
                    startLine.toString(),
                    headers.toString(),
                    ""
            );
        }
        return String.join(
                "\r\n",
                startLine.toString(),
                headers.toString(),
                "",
                body.getValue()
        );
    }
}
