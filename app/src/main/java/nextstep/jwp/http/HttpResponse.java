package nextstep.jwp.http;

import java.io.IOException;

public class HttpResponse {

    public static byte[] ok(String content, ContentType contentType) {
        return String.join(
                "\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getContentType() + "; charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content)
                .getBytes();
    }

    public static byte[] found(String redirect) {
        return String.join(
                "\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + redirect,
                redirect)
                .getBytes();
    }

    public static byte[] error(HttpError error) throws IOException {
        return String.join(
                "\r\n",
                "HTTP/1.1" + error.getCode() + " " + error.getName() + " ",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: " + error.getResource().getBytes().length + " ",
                "",
                error.getResource())
                .getBytes();
    }
}
