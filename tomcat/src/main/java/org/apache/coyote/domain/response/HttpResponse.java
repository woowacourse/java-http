package org.apache.coyote.domain.response;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.domain.request.requestline.HttpVersion;
import org.apache.coyote.domain.response.statusline.HttpStatusCode;
import org.apache.coyote.domain.response.statusline.StatusLine;

public class HttpResponse {

    private StatusLine statusLine;
    private ResponseBody responseBody;
    private final List<String> responseHeader;

    public HttpResponse() {
        this.responseHeader = new ArrayList<>();
    }

    public HttpResponse responseLine(HttpVersion httpVersion, HttpStatusCode httpStatusCode) {
        this.statusLine = StatusLine.of(httpVersion, httpStatusCode);
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

    public String getValue() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(statusLine.generateResponseString());
        for (String header : responseHeader) {
            stringBuilder.append(header);
        }
        if (responseBody != null) {
            stringBuilder.append(responseBody.getResponse());
        }
        return stringBuilder.toString();
    }
}
