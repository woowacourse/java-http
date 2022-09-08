package org.apache.coyote.domain.response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.domain.FilePath;
import org.apache.coyote.domain.HttpCookie;
import org.apache.coyote.domain.request.requestline.HttpVersion;

public class HttpResponse {

    private ResponseLine responseLine;
    private ResponseBody responseBody;
    private final List<String> responseHeader;

    public HttpResponse() {
        this.responseHeader = new ArrayList<>();
    }

    public HttpResponse(final ResponseLine responseLine,
                        final ResponseBody responseBody) {
        this.responseLine = responseLine;
        this.responseBody = responseBody;
        this.responseHeader = new ArrayList<>();
    }

    public HttpResponse responseLine(HttpVersion httpVersion, HttpStatusCode httpStatusCode) {
        this.responseLine = ResponseLine.of(httpVersion, httpStatusCode);
        return this;
    }

    public HttpResponse header(Header header) {
        responseHeader.add(header.getHeader());
        return this;
    }

    public HttpResponse responseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public static HttpResponse from(final HttpVersion httpVersion,
                                    final FilePath filePath,
                                    final HttpStatusCode httpStatusCode) throws URISyntaxException, IOException {
        return new HttpResponse(ResponseLine.of(httpVersion, httpStatusCode),
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
                .append("\r\n");
        for (String header : responseHeader) {
            stringBuilder.append(header);
        }
        if (responseBody != null) {
            stringBuilder.append(responseBody.getResponse());
        }
        return stringBuilder.toString();
    }
}
