package org.apache.coyote.domain.response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.domain.FilePath;
import org.apache.coyote.domain.HttpCookie;
import org.apache.coyote.domain.request.requestline.HttpVersion;

public class HttpResponse {

    private final ResponseLine responseLine;
    private final ContentType contentType;
    private final ResponseBody responseBody;
    private final List<String> responseHeader;

    public HttpResponse(final ResponseLine responseLine,
                        final ContentType contentType,
                        final ResponseBody responseBody) {
        this.responseLine = responseLine;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.responseHeader = new ArrayList<>();
    }

    public static HttpResponse from(final HttpVersion httpVersion,
                                    final FilePath filePath,
                                    final HttpStatusCode httpStatusCode) throws URISyntaxException, IOException {
        return new HttpResponse(ResponseLine.of(httpVersion, httpStatusCode),
                ContentType.find(filePath.getValue()),
                ResponseBody.from(filePath.getValue()));
    }

    public HttpResponse addRedirectUrlHeader(RedirectUrl redirectUrl) {
        responseHeader.add(redirectUrl.getHeader());
        return this;
    }

    public HttpResponse addSetCookieHeader(HttpCookie httpCookie) {
        responseHeader.add(httpCookie.getHeader());
        return this;
    }

    public String getValue() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\r\n")
                .append(responseLine.generateResponseString())
                .append("\r\n")
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
