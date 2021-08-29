package nextstep.jwp.util;

import java.io.IOException;

public class HttpResponse {

    private HeaderLine headerLine;

    public HttpResponse(HeaderLine headerLine) {
        this.headerLine = headerLine;
    }

    public String getResponse() throws IOException {
        return buildResponse(headerLine.getRequestURLWithoutQuery());
    }

    private String buildResponse(String path) throws IOException {
        final TranslatedFile translatedFile = new TranslatedFile(path);
        final String responseBody = translatedFile.staticValue();

        final String response = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
        return response;
    }

}
