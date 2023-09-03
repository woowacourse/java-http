package org.apache.response;

public class HttpResponse {

    public static String create(String content, String url) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + getContentType(url) + ";charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);
    }

    private static String getContentType(final String url) {
        if (url.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
