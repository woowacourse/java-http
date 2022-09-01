package org.apache.coyote.http11.model.response;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.Headers;

public class Response {

    private final Status status;
    private final Headers headers;
    private final ResponseBody responseBody;

    public Response(final Status status, final String url) throws IOException {
        this.status = status;
        this.headers = new Headers(new ArrayList<>());
        this.responseBody = new ResponseBody(url);
        this.headers.add(new Header("Content-Type", responseBody.getContentType() + ";charset=utf-8"));
        this.headers.add(new Header("Content-Length", String.valueOf(responseBody.getContentLength())));
    }

    public String getString() {
        return String.join("\r\n",
                "HTTP/1.1" + " " + status.getCode() + " " + status.name() + " ",
                headers.getString(),
                "",
                responseBody.getBody());
    }
}
