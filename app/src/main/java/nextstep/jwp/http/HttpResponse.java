package nextstep.jwp.http;

public class HttpResponse {
    public static String ok(String contentType, String payload) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + payload.getBytes().length + " ",
                "",
                payload);
    }

    public static String redirect(String redirectTo) {
        return String.join("\r\n",
                "HTTP/1.1 301 Redirect ",
                "Location: " + redirectTo);
    }

    public static String found(String redirectTo) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + redirectTo);
    }

    public static String unauthorized() {
        return String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ");
    }
}
