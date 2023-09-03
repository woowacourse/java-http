package org.apache.coyote.http11.response;

public class Http11Response {

    final String body;

    public Http11Response(final String body) {
        this.body = body;
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
