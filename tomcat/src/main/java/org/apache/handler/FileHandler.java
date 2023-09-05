package org.apache.handler;

import java.io.IOException;
import org.apache.common.ContentType;
import org.apache.common.FileReader;
import org.apache.common.HttpStatus;
import org.apache.request.HttpRequest;
import org.apache.response.HttpResponse;

public class FileHandler implements RequestHandler {

    private static final String METHOD_NOT_ALLOWED_PAGE = "/405.html";

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        if (!httpRequest.isGet() && !httpRequest.isPost()) {
            String content = FileReader.read(METHOD_NOT_ALLOWED_PAGE);
            return new HttpResponse(HttpStatus.METHOD_NOT_ALLOWED, ContentType.TEXT_HTML, content);
        }

        String target = httpRequest.getTarget();
        String content = FileReader.read(target);
        return new HttpResponse(HttpStatus.OK, ContentType.from(target), content);
    }
}
