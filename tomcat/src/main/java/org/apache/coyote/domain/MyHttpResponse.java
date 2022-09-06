package org.apache.coyote.domain;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MyHttpResponse {

    private final HttpStatusCode httpStatusCode;
    private final ContentType contentType;
    private final ResponseBody responseBody;
    private final List<String> responseHeader;

    public MyHttpResponse(HttpStatusCode httpStatusCode, ContentType contentType, ResponseBody responseBody) {
        this.httpStatusCode = httpStatusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.responseHeader = new ArrayList<>();
    }

    public static MyHttpResponse from(FilePath filePath, HttpStatusCode httpStatusCode)
            throws URISyntaxException, IOException {
        return new MyHttpResponse(httpStatusCode,
                ContentType.find(filePath.getValue()),
                ResponseBody.from(filePath.getValue()));
    }

    public MyHttpResponse addRedirectUrlHeader(RedirectUrl redirectUrl) {
        responseHeader.add(redirectUrl.getHeader());
        return this;
    }

    public MyHttpResponse addSetCookieHeader(HttpCookie httpCookie) {
        responseHeader.add(httpCookie.getHeader());
        return this;
    }

    public String getValue() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\r\n")
                .append("HTTP/1.1 " + httpStatusCode.getStatusCode() + " " + httpStatusCode.getStatusMessage() + " "
                        + "\r\n")
                .append("Content-Type: " + this.contentType.getType() + ";charset=utf-8 " + "\r\n");
        for (String header : responseHeader) {
            stringBuilder.append(header).append("\r\n");
        }
        stringBuilder.append("Content-Length: " + this.responseBody.getValue().getBytes().length + " " + "\r\n")
                .append("\r\n")
                .append(this.responseBody.getValue());
        return stringBuilder.toString();
    }
}
