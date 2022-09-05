package org.apache.coyote.domain;

import java.io.IOException;
import java.net.URISyntaxException;

public class MyHttpResponse {

    private final HttpStatusCode httpStatusCode;
    private final ContentType contentType;
    private final ResponseBody responseBody;
    private final RedirectUrl redirectUrl;

    public MyHttpResponse(HttpStatusCode httpStatusCode, ContentType contentType, ResponseBody responseBody,
                          RedirectUrl redirectUrl) {
        this.httpStatusCode = httpStatusCode;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.redirectUrl = redirectUrl;
    }

    public static MyHttpResponse from(FilePath filePath, HttpStatusCode httpStatusCode, RedirectUrl redirectUrl)
            throws URISyntaxException, IOException {
        return new MyHttpResponse(httpStatusCode,
                ContentType.find(filePath.getValue()),
                ResponseBody.from(filePath.getValue()),
                redirectUrl);
    }

    public static MyHttpResponse from(FilePath filePath, HttpStatusCode httpStatusCode)
            throws URISyntaxException, IOException {
        return new MyHttpResponse(httpStatusCode,
                ContentType.find(filePath.getValue()),
                ResponseBody.from(filePath.getValue()),
                null);
    }

    public String getValue() {
        if(redirectUrl == null){
            return String.join("\r\n",
                    "HTTP/1.1 " + httpStatusCode.getStatusCode() + " " + httpStatusCode.getStatusMessage() + " ",
                    "Content-Type: " + this.contentType.getType() + ";charset=utf-8 ",
                    "Content-Length: " + this.responseBody.getValue().getBytes().length + " ",
                    "",
                    this.responseBody.getValue());
        }
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatusCode.getStatusCode() + " " + httpStatusCode.getStatusMessage() + " ",
                "Content-Type: " + this.contentType.getType() + ";charset=utf-8 ",
                "Content-Length: " + this.responseBody.getValue().getBytes().length + " ",
                redirectUrl.getHeader(),
                "",
                this.responseBody.getValue());
    }
}
