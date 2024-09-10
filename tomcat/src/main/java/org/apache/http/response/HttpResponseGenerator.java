package org.apache.http.response;

public class HttpResponseGenerator {

    private HttpResponseGenerator() {
    }

    public static String getOkResponse(String mimeType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + mimeType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public static String getFoundResponse(String url) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location:  " + url);
    }

    public static String getNotFountResponse() {
        return String.join("\r\n",
                "HTTP/1.1 404 Not Found ");
    }

    public static String getUnauthorizedResponse() {
        return String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ");
    }

    public static String getInternalServerErrorResponse() {
        return String.join("\r\n",
                "HTTP/1.1 401 Internal Server Error ");
    }
}
