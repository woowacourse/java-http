package org.apache.coyote.http11.httpresponse;

import org.apache.coyote.http11.FileReader;

import java.io.IOException;

public class HttpResponse {

    private final String response;

    private HttpResponse(String response) {
        this.response = response;
    }

    public static HttpResponse of(String location, HttpStatusCode httpStatusCode) {
        try {
            String contentType = FileReader.probeContentType(location);
            String responseBody = FileReader.read(location);
            String response = createResponse(httpStatusCode.getStatusCodeMessage(), contentType, responseBody);

            return new HttpResponse(response);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.", e);
        }
    }

    private static String createResponse(String statusCodeMessage, String contentType, String responseBody) {
        return String.join("\r\n",
                String.format("HTTP/1.1 %s ", statusCodeMessage),
                String.format("Content-Type: %s;charset=utf-8 ", contentType),
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public String getResponse() {
        return response;
    }
}
