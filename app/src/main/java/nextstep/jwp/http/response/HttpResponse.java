package nextstep.jwp.http.response;

import java.util.Arrays;
import java.util.List;

/**
 * Http/1.1 200 OK                       // Status Line Date: Thu, 20 May 2005 21:12:24 GMT   // General Headers
 * Connection: close Server: Apache/1.3.22                 // Response Headers Accept-Ranges: bytes Content-Type:
 * text/html               // Entity Headers Content-Length: 170 last-Modified: Tue, 14 May 2004 10:13:35 GMT
 *
 * <html>                                 // Message Body
 * <head>
 * ..
 * </head>>
 * </html>
 */

public class HttpResponse {

    private final String statusLine;
    private final List<String> headers;
    private final String messageBody;

    public HttpResponse(String statusLine, List<String> headers, String messageBody) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    public static HttpResponse ok(String messageBody) {
        String statusLine = "HTTP/1.1 200 OK ";
        List<String> headers = Arrays.asList(
                "Content-Type: text/html;charset=utf-8 ",
                "Location: /index.html ",
                "Content-Length: " + messageBody.getBytes().length + " ",
                ""
        );
        return new HttpResponse(statusLine, headers, messageBody);
    }

    public static HttpResponse redirect(String path){
        String statusLine = "HTTP/1.1 302 Found ";
        List<String> headers = Arrays.asList(
                "Content-Type: text/html;charset=utf-8 ",
                "Location: "+path+" ",
                ""
        );
        return new HttpResponse(statusLine, headers, "");
    }

    public static HttpResponse error(String messageBody){
        String statusLine = "HTTP/1.1 500 Internal Server Error ";
        List<String> headers = Arrays.asList(
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + messageBody.getBytes().length + " ",
                ""
        );
        return new HttpResponse(statusLine, headers, messageBody);
    }

    public static HttpResponse unauthorized(String messageBody){
        String statusLine = "HTTP/1.1 401 Unauthorized ";
        List<String> headers = Arrays.asList(
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + messageBody.getBytes().length + " ",
                ""
        );
        return new HttpResponse(statusLine, headers, messageBody);
    }

    public byte[] responseAsBytes(){
        return String.join("\r\n",
                statusLine,
                String.join("\r\n", headers),
                messageBody).getBytes();
    }
}
