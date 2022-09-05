package org.apache.coyote.response;

import java.util.Map;
import org.apache.coyote.request.HttpRequest;

public class HttpResponse {

    public static final String ACCEPT = "Accept";

    private final HttpStatus httpStatus;
    private final String contentType;
    private String responseBody;

    public HttpResponse(final HttpStatus httpStatus, final String contentType, final String responseBody) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final HttpStatus httpStatus, final HttpRequest httpRequest,
                                  final String responseBody) {
        Map<String, String> headers = httpRequest.getHttpHeaders();
        return new HttpResponse(httpStatus, headers.get(ACCEPT), responseBody);
    }

    public byte[] getResponse() {
        return createResponse().getBytes();
    }

    private String createResponse() {
        return String.join("\r\n",
                String.format("HTTP/1.1 %d %s", httpStatus.getCode(), httpStatus.getMessage()) +
                        String.format("Content-Type: %s;charset=utf-8 ", contentType),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
