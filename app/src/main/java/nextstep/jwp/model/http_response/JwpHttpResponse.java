package nextstep.jwp.model.http_response;

public class JwpHttpResponse {

    private JwpHttpResponse() {
    }

    public static String ok(String resourceFile) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + resourceFile.getBytes().length + " ",
                "",
                resourceFile);
    }

    public static String found(String redirectUri) {
        return String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: http://localhost:8080/" + redirectUri + " ",
                "",
                "");
    }
}
