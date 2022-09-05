package org.apache.coyote.domain;

import java.io.IOException;
import java.net.URISyntaxException;

public class MyHttpResponse {

    private final ContentType contentType;
    private final ResponseBody responseBody;

    public MyHttpResponse(ContentType contentType, ResponseBody responseBody) {
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public static MyHttpResponse from(FilePath filePath) throws URISyntaxException, IOException {
        return new MyHttpResponse(ContentType.find(filePath.getValue()), ResponseBody.from(filePath.getValue()));
    }

    public String getValue() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + this.contentType.getType() + ";charset=utf-8 ",
                "Content-Length: " + this.responseBody.getValue().getBytes().length + " ",
                "",
                this.responseBody.getValue());
    }
}
