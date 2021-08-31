package nextstep.jwp.model;

public class CustomHttpResponse {

    public static String found(String redirectUri) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: http://localhost:8080/"+ redirectUri +" ",
                "",
                "");
    }

    public static String ok(String contentType, String fileSource) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + fileSource.getBytes().length + " ",
                "",
                fileSource);
    }

    public static String notFound(String redirectUri) {
        return String.join("\r\n",
                "HTTP/1.1 404 NOT FOUND ",
                "Location: http://localhost:8080/"+ redirectUri +" ",
                "",
                "");
    }
}
