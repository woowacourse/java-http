package nextstep.jwp;

public class Fixture {

    public static String getRequest(String url) {
        return String.join("\r\n",
                "GET "+ url +" HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }

    public static String getRequest(String url, String cookie) {
        return String.join("\r\n",
                "GET "+ url +" HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: " + cookie + " ",
                "",
                "");
    }

    public static String postFormDataRequest(String url, String body) {
        return String.join("\r\n",
                "POST "+ url +" HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                body);
    }
}
