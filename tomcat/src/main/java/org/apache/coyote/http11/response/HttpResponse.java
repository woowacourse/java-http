package org.apache.coyote.http11.response;

public class HttpResponse {

    private static final String DELIMITER = "\r\n";
    private static final String BLANK_LINE = "";
    private final String response;

    public HttpResponse(String response) {
        this.response = response;
    }

    public static HttpResponse of(String status, String requestURI, String responseBody) {
        String formatResponse = String.join(
                DELIMITER,
                "HTTP/1.1 " + status + " ",
                "Content-Type: " + contentType(requestURI) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                BLANK_LINE,
                responseBody
        );
        return new HttpResponse(formatResponse);
    }

    public static HttpResponse from(ResponseEntity responseEntity) {
        HttpStatus httpStatus = responseEntity.getHttpStatus();
        String status = httpStatus.getStatusCode() + " " + httpStatus.name();
        return of(status, responseEntity.getRequestURI(), responseEntity.getResponseBody());
    }

    private static String contentType(String requestURI) {
        if (requestURI.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    public String getResponse() {
        return response;
    }
}
