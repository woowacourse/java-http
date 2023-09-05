package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ContentType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private final StatusCode statusCode;
    private final ResponseHeader header;
    private final String responseBody;

    public HttpResponse(final StatusCode statusCode,
                        final ContentType contentType,
                        final String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;

        Map<String, String> newHeader = new LinkedHashMap<>();
        newHeader.put("Content-Type", contentType.getContentType() + ";charset=utf-8");
        newHeader.put("Content-Length", String.valueOf(responseBody.getBytes().length));
        header = new ResponseHeader(newHeader);
    }

    public HttpResponse(final StatusCode statusCode) {
        this.statusCode = statusCode;
        this.responseBody = "";
        header = new ResponseHeader(new LinkedHashMap<>());
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode.getCode() + " " + statusCode.getMessage() + " ",
                printHeader() + "\n",
                responseBody);
    }

    public String getRedirectResponse(String redirectUrl) {
        header.addHeader("Location", redirectUrl);
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode.getCode() + " " + statusCode.getMessage() + " ",
                printHeader() + "\n",
                "Location: " + redirectUrl + " \r/n",
                "",
                "");
    }

    public void addJSessionId(final String JSessionId) {
        header.addHeader("Set-Cookie", "JSESSIONID=" + JSessionId);
    }

    public String printHeader() {
        return header.getHeader().entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " \r")
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
