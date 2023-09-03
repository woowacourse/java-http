package org.apache.coyote.http11;

public class HttpResponse {

    private static final String DELIMITER = "\r\n";
    private static final String BLANK_LINE = "";
    private final String response;

    public HttpResponse(String response) {
        this.response = response;
    }

    public static HttpResponse of(String requestURI, String responseBody) {
        String formatResponse = String.join(
                DELIMITER,
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType(requestURI) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                BLANK_LINE,
                responseBody
        );
        return new HttpResponse(formatResponse);
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
