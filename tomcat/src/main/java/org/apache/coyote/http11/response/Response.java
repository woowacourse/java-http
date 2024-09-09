package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.Request;

public class Response {
    public static String writeAsStaticResource(Request request, String contentType, String content) {
        if (content == null) {
            return null;
        }
        return String.join("\r\n",
                String.format("%s 200 OK ", request.getHttpVersion()),
                String.format("Content-Type: %s;charset=utf-8 ",contentType),
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);
    }

    public static String writeAsFound(Request request, String target) {
        return String.join("\r\n",
                String.format("%s 301 FOUND ", request.getHttpVersion()),
                String.format("Location: %s ",target),
                "\r\n");
    }
}
