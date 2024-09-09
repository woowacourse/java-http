package org.apache.http.response;

public class HttpResponseGenerator {
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
                "HTTP/1.1 302 FOUND ",
                "Location:  " + url);
    }
}
