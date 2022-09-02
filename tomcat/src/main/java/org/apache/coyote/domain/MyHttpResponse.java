package org.apache.coyote.domain;

import java.io.IOException;
import java.net.URISyntaxException;

public class MyHttpResponse {

    private final String value;

    private MyHttpResponse(String value) {
        this.value = value;
    }

    public static MyHttpResponse from(FilePath filePath) throws URISyntaxException, IOException {
        ContentType contentType = ContentType.find(filePath.getValue());
        ResponseBody responseBody = ResponseBody.from(filePath.getValue());
        return new MyHttpResponse(generateResponse(contentType, responseBody));
    }

    private static String generateResponse(ContentType contentType, ResponseBody responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getValue().getBytes().length + " ",
                "",
                responseBody.getValue());
    }

    public String getValue() {
        return value;
    }
}
