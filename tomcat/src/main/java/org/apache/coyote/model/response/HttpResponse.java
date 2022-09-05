package org.apache.coyote.model.response;

import org.apache.coyote.model.request.HttpRequest;

public class HttpResponse {

    private static final String HTTP_RESPONSE_VALUE = "HTTP/1.1 200 OK ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_DELIMITER = ": ";

    public static String createResponse(HttpRequest httpRequest, String responseBody) {
        return String.join("\r\n",
                HTTP_RESPONSE_VALUE,
                CONTENT_TYPE + HEADER_DELIMITER + httpRequest.getContentType() + ";charset=utf-8 ",
                CONTENT_LENGTH + HEADER_DELIMITER + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
