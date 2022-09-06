package org.apache.coyote.http11.response;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpCookie;

public class Http11Response {

    private final String contentType;
    private final HttpStatus httpStatus;
    private final String resource;
    private final HttpCookie cookie;

    public Http11Response(String path, HttpStatus httpStatus, String resource) {
        this.contentType = ContentType.from(path);
        this.httpStatus = httpStatus;
        this.resource = resource;
        cookie = new HttpCookie();
    }

    public Http11Response(String path, HttpStatus httpStatus, String resource, HttpCookie cookie) {
        this.contentType = ContentType.from(path);
        this.httpStatus = httpStatus;
        this.resource = resource;
        this.cookie = cookie;
    }

    public String toResponse() throws IOException {
        if (cookie.isEmpty()) {
            return String.join("\r\n",
                    "HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.getName() + " ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + resource.getBytes().length + " ",
                    "",
                    resource);
        }
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.getName() + " ",
                cookie.getResponse(),
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + resource.getBytes().length + " ",
                "",
                resource);
    }

    public String getResource() {
        return resource;
    }
}
