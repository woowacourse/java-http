package org.apache.handler;

import java.io.IOException;
import org.apache.common.ContentType;
import org.apache.common.FileReader;
import org.apache.common.HttpStatus;
import org.apache.request.HttpRequest;
import org.apache.response.HttpResponse;

public class DefaultHandler implements RequestHandler {

    private static final String DEFAULT_RESPONSE = "Hello world!";
    private static final String METHOD_NOT_ALLOWED_PAGE = "/405.html";

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        if (!httpRequest.isGet()) {
            String content = FileReader.read(METHOD_NOT_ALLOWED_PAGE);
            HttpResponse httpResponse = new HttpResponse(HttpStatus.METHOD_NOT_ALLOWED, content);
            httpResponse.setContentType(ContentType.TEXT_HTML);
            return httpResponse;
        }
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, DEFAULT_RESPONSE);
        httpResponse.setContentType(ContentType.TEXT_HTML);
        return httpResponse;
    }
}
