package org.apache.coyote.http11.message.response;

import org.apache.coyote.http11.message.body.HttpBody;
import org.apache.coyote.http11.message.header.HttpHeaders;

public class HttpResponse {

    private HttpStatusLine statusLine;
    private HttpHeaders header;
    private HttpBody httpBody;

    public HttpResponse(
            final HttpStatusLine statusLine,
            final HttpHeaders header,
            final HttpBody httpBody
    ) {
        this.statusLine = statusLine;
        this.header = header;
        this.httpBody = httpBody;
    }

    public void setStatusLine(final HttpStatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void setHeader(final HttpHeaders header) {
        this.header = header;
    }

    public void setHttpBody(final HttpBody httpBody) {
        this.httpBody = httpBody;
    }

    public String convertHttpResponseMessage() {
        return String.join("\r\n",
                statusLine.convertHttpStatusLineMessage(),
                header.convertHeaderMessage(),
                httpBody.convertResponseMessage()
        );
    }
}
