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

    public static String badRequest() {
        return String.join("\r\n",
                "HTTP/1.1 400 Bad Request ");
    }

    public static String unauthorized() {
        return String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ");
    }

    public static String notFound() {
        return String.join("\r\n",
                "HTTP/1.1 404 Not Found ");
    }

    public static String methodNotAllowed() {
        return String.join("\r\n",
                "HTTP/1.1 405 Method Not Allowed ");
    }

    public static String internalServerError() {
        return String.join("\r\n",
                "HTTP/1.1 500 Internal Server Error ");
    }
}
