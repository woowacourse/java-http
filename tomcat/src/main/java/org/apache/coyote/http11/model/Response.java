package org.apache.coyote.http11.model;

public class Response {

    private final Status status;
    private final Headers headers;
    private final String body;

    public Response(final Status status, final Headers headers, final String body) {
        this.status = status;
        this.headers = headers;
        this.headers.add(new Header("Content-Length", String.valueOf(body.getBytes().length)));
        this.body = body;
    }

    public String getString() {
        return String.join("\r\n",
                "HTTP/1.1" + " " + status.getCode() + " " + status.name() + " ",
                headers.getString(),
                "",
                body);
    }
}
