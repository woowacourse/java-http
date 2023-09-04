package nextstep.jwp.handler;

import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class HttpResponseParser {

    public static byte[] parse(HttpResponse response) {
        HttpStatus status = response.getHttpStatus();
        String result = String.join("\r\n",
            "HTTP/1.1 " + status.code + " " + status.name() + " ",
            parseHeaders(response) + parseCookie(response.getCookie()),
            "",
            response.getBody());
        System.out.println("result = " + result);
        return result.getBytes();
    }

    private static String parseHeaders(HttpResponse response) {
        return response.getHeaders().entrySet().stream()
            .map(it -> String.join(": ", it.getKey(), it.getValue() + " "))
            .collect(Collectors.joining("\r\n"));
    }

    private static String parseCookie(HttpCookie cookie) {
        if (cookie.getCookies().size() == 0) {
            return "";
        }
        return "\nSet-Cookie: " + cookie.getCookies().entrySet().stream()
            .map(it -> String.join("=", it.getKey(), it.getValue() + " "))
            .collect(Collectors.joining());
    }
}
