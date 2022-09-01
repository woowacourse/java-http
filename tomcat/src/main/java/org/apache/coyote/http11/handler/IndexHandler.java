package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.ResourceUtil;

public class IndexHandler implements Handler {

    private final HttpRequest httpRequest;

    public IndexHandler(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String getResponse() {
        String responseBody = ResourceUtil.getResponseBody("/index.html", getClass());

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + "text/html" + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
