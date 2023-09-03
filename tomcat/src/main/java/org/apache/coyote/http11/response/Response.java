package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.RequestHeader;

import java.util.HashMap;
import java.util.Map;

public class Response {
    //
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String LOCATION_HEADER = "Location";
    private final HttpStatus httpStatus;
    private final Map<String, String> headers;
    private final String body;

    public Response(final HttpStatus httpStatus, final Map<String, String> headers, final String body) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public static Response ok(final String responseBody, RequestHeader requestHeader) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE_HEADER, requestHeader.getHeaderValue("Accept").split(",")[0]+";charset=utf-8");
//        headers.put(CONTENT_TYPE_HEADER, "text/html;charset=utf-8");
        headers.put(CONTENT_LENGTH_HEADER, String.valueOf(responseBody.getBytes().length));
        return new Response(HttpStatus.OK, headers, responseBody);
    }

    public static Response notFound() {
        Map<String, String> headers = new HashMap<>();
        headers.put(LOCATION_HEADER, "/404.html");
        return new Response(HttpStatus.NOT_FOUND, headers, null);

    }

    public static Response redirection(String redirectionUrl) {
        Map<String, String> headers = new HashMap<>();
        headers.put(LOCATION_HEADER, redirectionUrl);
        return new Response(HttpStatus.REDIRECTION, headers, null);
    }


    public String toString() {
        String responseLine = String.join(" ", "HTTP/1.1", String.valueOf(httpStatus.getCode()), httpStatus.getMessage());
        String responseHeader = convertHeaderToString();

        if (body == null) {
            return String.join("\r\n", responseLine + " ",
                    responseHeader + " ",
                    "");
        }
        return String.join("\r\n", responseLine + " ",
                responseHeader + " ",
                "",
                body);
    }


    private String convertHeaderToString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(" \r\n");
        }
        return sb.toString();
    }

}
