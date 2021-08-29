package nextstep.jwp.http.response;

import nextstep.jwp.handler.Model;
import nextstep.jwp.view.View;

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

    private static final String HTTP_REQUEST_LINE_FORMAT = "HTTP/1.1 %s %s ";
    private static final String HEADER_FORMAT = "%s: %s ";
    private static final String SEPARATE_LINE = "";

    private final String statusLine;
    private final ResponseHeaders responseHeaders;
    private final String messageBody;

    public HttpResponse(String statusLine, ResponseHeaders responseHeaders, String messageBody) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.messageBody = messageBody;
    }

    public static HttpResponse of(Model model, View view) {
        HttpStatus httpStatus = model.httpStatus();
        String statusLine = String.format(HTTP_REQUEST_LINE_FORMAT, httpStatus.code(), httpStatus.name());

        ResponseHeaders headers = new ResponseHeaders();

        if (!view.isEmpty()) {
            headers.addAttribute("Content-Type", view.contentType());
            headers.addAttribute("Content-Length", view.contentLength());
        }

        if (model.contains("Location")) {
            headers.addAttribute("Location", (String) model.getAttribute("Location"));
        }

        return new HttpResponse(statusLine, headers, view.content());
    }

    public byte[] responseAsBytes() {
        return responseAsString().getBytes();
    }

    public String responseAsString() {
        return String.join("\r\n",
                statusLine,
                String.join("\r\n", responseHeaders.asLines(HEADER_FORMAT)),
                SEPARATE_LINE,
                messageBody);
    }
}
