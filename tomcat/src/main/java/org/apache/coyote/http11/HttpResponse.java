package org.apache.coyote.http11;

import java.io.IOException;

public class HttpResponse {

    private static final ResponseBodyGenerator RESPONSE_BODY_GENERATOR = new ResponseBodyGenerator();

    private final ContentType contentType;
    private final String responseBody;

    public HttpResponse(String responseBody, ContentType contentType) {
        this.responseBody = responseBody;
        this.contentType = contentType;
    }

    public static HttpResponse from(String fileSource) throws IOException {
        return new HttpResponse(
                RESPONSE_BODY_GENERATOR.generate(fileSource), ContentType.from(fileSource));
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                contentType.getValue(),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
