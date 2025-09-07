package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.ResponseCookie;

public class Http11OutputBuffer {

    private final OutputStream outputStream;

    public Http11OutputBuffer(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(HttpResponse httpResponse) throws IOException {
        outputStream.write(parseToString(httpResponse).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    public String parseToString(HttpResponse httpResponse) {
        StringBuilder responseBuilder = new StringBuilder();

        responseBuilder.append(String.format("%s %d %s ", httpResponse.httpVersion(), httpResponse.statusCode(),
                httpResponse.status()));
        responseBuilder.append("\r\n");

        if (httpResponse.contentType() != null) {
            responseBuilder.append("Content-Type: ").append(httpResponse.contentType());
            if (httpResponse.charSet() != null) {
                responseBuilder.append(";").append(httpResponse.charSet()).append(" ");
            }
            responseBuilder.append("\r\n");
        }

        if (httpResponse.location() != null) {
            responseBuilder.append("Location: ").append(httpResponse.location()).append(" ");
            responseBuilder.append("\r\n");
        }

        if (httpResponse.contentLength() > 0) {
            responseBuilder.append("Content-Length: ").append(httpResponse.contentLength()).append(" ");
            responseBuilder.append("\r\n");
        }

        if (httpResponse.responseCookie() != null) {
            addCookie(httpResponse.responseCookie(), responseBuilder);
        }

        responseBuilder.append("\r\n");

        if (httpResponse.responseBody() != null) {
            responseBuilder.append(httpResponse.responseBody());
        }

        return responseBuilder.toString();
    }

    private void addCookie(ResponseCookie responseCookie, StringBuilder responseBuilder) {
        for (String key : responseCookie.getCookieValues().keySet()) {
            responseBuilder.append("Set-Cookie: ").append(key + "=" + responseCookie.getCookieValues().get(key));
        }
    }
}
