package nextstep.jwp.http;

import java.io.IOException;
import nextstep.jwp.RequestHandler;
import nextstep.jwp.controller.HttpError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public static byte[] ok(String content) {
        return String.join(
                "\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.length() + " ",
                "",
                content)
                .getBytes();
    }

    public static byte[] found(String content) {
        return String.join(
                "\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.length() + " ",
                "",
                content)
                .getBytes();
    }

    public static byte[] error(HttpError exception) throws IOException {
        return String.join(
                "\r\n",
                "HTTP/1.1" + exception.getCode() + " " + exception.getName() + " ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + exception.getResource().length() + " ",
                "",
                exception.getResource())
                .getBytes();
    }
}
