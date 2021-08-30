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

    public static byte[] found(String content, ContentType contentType) {
        return String.join(
                "\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: " + contentType.getContentType() + "; charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content)
                .getBytes();
    }

    public static byte[] error(HttpError exception) throws IOException {
        return String.join(
                "\r\n",
                "HTTP/1.1" + exception.getCode() + " " + exception.getName() + " ",
                "Content-Type: text/html; charset=utf-8 ",
                "Content-Length: " + exception.getResource().length() + " ",
                "",
                exception.getResource())
                .getBytes();
    }
}
