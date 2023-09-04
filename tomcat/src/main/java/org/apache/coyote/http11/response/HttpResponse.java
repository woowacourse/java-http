package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpResponse {

    private final StatusCode statusCode;
    private final Map<String, String> header;
    private final String responseBody;

    public HttpResponse(final StatusCode statusCode,
                        final ContentType contentType,
                        final String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;

        header = new HashMap<>();
        header.put("Content-Type", contentType.getContentType()+";charset=utf-8");
        header.put("Content-Length", String.valueOf(responseBody.getBytes().length));
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                printHeader() + "\n",
                responseBody);
    }

//    public String getRedirectResponse() {
//        return String.join("\r\n",
//                "HTTP/1.1 " + statusCode + " ",
//                "HTTP/1.1 " + statusCode + "\r",
//                "Location: " + header.get("Location: ") + "\r",
//                "Content-Type: " + header.get("Content-Type: ") + "\r",
//                "Content-Length: " + header.get("Content-Length: ") + "\r",
//                "Set-Cookie: " + header.get("Set-Cookie: ") + "\r\n",
//                responseBody);
//    }

    public void addJSessionId(final HttpRequest httpRequest) {
        if (!httpRequest.hasJSessionId()) {
            System.out.println("쿠키를 담았다.");
            header.put("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
        }
    }

    public String printHeader() {
        return header.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
