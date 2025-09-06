package org.apache.coyote.http11;

public class Http11OutputBuffer {
    public static String parseToString(HttpResponse httpResponse) {
        StringBuilder responseBuilder = new StringBuilder();

        responseBuilder.append(String.format("HTTP/%.1f %d %s ", httpResponse.httpVersion(), httpResponse.statusCode(),
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
            responseBuilder.append("Location: ").append(httpResponse.location());
            responseBuilder.append("\r\n");
        }

        responseBuilder.append("Content-Length: ").append(httpResponse.contentLength()).append(" ");
        responseBuilder.append("\r\n");

        responseBuilder.append("\r\n");

        if (httpResponse.responseBody() != null) {
            responseBuilder.append(httpResponse.responseBody());
        }

        return responseBuilder.toString();
    }
}
