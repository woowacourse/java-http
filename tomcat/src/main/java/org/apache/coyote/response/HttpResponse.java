package org.apache.coyote.response;

import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.util.FileTypeChecker;

public class HttpResponse {

    private final HttpResponseStartLine startLine;
    private final HttpResponseHeader headers;
    private final HttpResponseBody body;

    public HttpResponse(HttpResponseStartLine startLine, HttpResponseHeader headers, HttpResponseBody body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse from(HttpRequest httpRequest) {
        HttpResponseStartLine startLine = HttpResponseStartLine.defaultStartLineFrom(httpRequest);
        return new HttpResponse(startLine, new HttpResponseHeader(), new HttpResponseBody(null));
    }

    public void addContentType(String contentType) {
        headers.addContentType(contentType);
    }

    public void addBody(String newBody) {
        this.body.update(newBody);
        if (this.body.hasBody()) {
            headers.addContentLength(newBody.getBytes().length);
            return;
        }
        headers.addContentLength(0);
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
