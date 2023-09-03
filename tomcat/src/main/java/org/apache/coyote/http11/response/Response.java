package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Response {
    private final HttpStatus status;
    private final String contentType;
    private final String responseBody;

    public Response(HttpStatus status, String contentType, String responseBody) {
        this.status = status;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public String getRequest(){
        return String.join("\r\n",
                "HTTP/1.1 " + status ,
                "Content-Type: text/" + contentType + ";",
                "charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public String redirect(String redirectUri){
        return String.join("\r\n",
                "HTTP/1.1 " + status ,
                "Content-Type: text/" + contentType + ";",
                "charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "location : " + redirectUri,
                "",
                responseBody);
    }

    public static Response of(HttpStatus status, String contentType, String responseBody){
        return new Response(status,contentType,responseBody);
    }

    public static Response badResponseFrom(HttpStatus status){
        return new Response(status, "html", status.getFile());
    }
}
