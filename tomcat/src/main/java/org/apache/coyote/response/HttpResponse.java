package org.apache.coyote.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.request.HttpHeader;

public class HttpResponse {

    private final HttpStatusCode httpStatusCode;
    private final HttpHeader responseHeader;
    private final String responseBody;

    public HttpResponse(HttpStatusCode httpStatusCode, String responseBody, ContentType contentType) {
        this.httpStatusCode = httpStatusCode;
        this.responseHeader = buildInitialHeaders(responseBody, contentType);
        this.responseBody = responseBody;
    }

    private HttpHeader buildInitialHeaders(String responseBody, ContentType contentType) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_LENGTH.getName(), responseBody.getBytes().length + " ");
        headers.put(HttpHeaders.CONTENT_TYPE.getName(), contentType.getName() + ";charset=utf-8 ");
        return new HttpHeader(headers);
    }

    public void addHeader(String name, String value) {
        responseHeader.add(name, value);
    }

    public String buildMessage() {
        return String.join("\r\n",
                httpStatusCode.buildMessage(),
                responseHeader.buildMessage(),
                "",
                responseBody
        );
    }
}
