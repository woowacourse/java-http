package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final String LINE_SEPARATOR = "\r\n";
    private static final String STATUS_LINE_FORMAT = "%s %d %s ";
    private static final String HEADER_FORMAT = "%s: %s ";

    private final Protocol protocol = Protocol.HTTP11;
    private final HttpStatus httpStatus;
    private final Map<String, String> headers;
    private final String responseBody;

    public HttpResponse(HttpStatus httpStatus, Map<String, String> headers) {
        this(httpStatus, headers, "");
    }

    public HttpResponse(HttpStatus httpStatus, Map<String, String> headers, String responseBody) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse createRedirectResponse(HttpStatus httpStatus, String location) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", location);
        return new HttpResponse(
                httpStatus,
                headers
        );
    }

    public static HttpResponse createTextResponse(HttpStatus httpStatus, String responseBody) {
        Map<String, String> headers = new HashMap<>();
        int contentLength = responseBody.getBytes().length;
        headers.put("Content-Type", "text/plain;charset=utf-8 ");
        headers.put("Content-Length", String.valueOf(contentLength));

        return new HttpResponse(
                httpStatus,
                headers,
                responseBody
        );
    }

    public static HttpResponse createFileResponse(ResponseFile responseFile) {
        Map<String, String> headers = new HashMap<>();
        String responseBody = responseFile.getContent();
        int contentLength = responseBody.getBytes().length;
        headers.put("Content-Type", responseFile.getContentType());
        headers.put("Content-Length", String.valueOf(contentLength));

        return new HttpResponse(
                HttpStatus.OK,
                headers,
                responseBody
        );
    }

    public String toResponse() {
        String statusLine = getStatusLine();
        String headers = getHeaders();

        return String.join(
                LINE_SEPARATOR,
                statusLine,
                headers,
                "",
                responseBody
        );
    }

    private String getStatusLine() {
        return String.format(STATUS_LINE_FORMAT,
                protocol.getName(),
                httpStatus.getCode(),
                httpStatus.getReasonPhrase()
        );
    }

    private String getHeaders() {
        List<String> formattedHeaders = new ArrayList<>();
        for (String headerKey : headers.keySet()) {
            String headerValue = headers.get(headerKey);
            String formattedHeader = String.format(HEADER_FORMAT, headerKey, headerValue);
            formattedHeaders.add(formattedHeader);
        }
        return String.join(LINE_SEPARATOR, formattedHeaders);
    }

//    public void addSession(Session session) {
//        if (headers.containsKey("JSESSIONID")) {
//            return;
//        }
//        headers.put("JSESSIONID", session.getId());
//    }
}
