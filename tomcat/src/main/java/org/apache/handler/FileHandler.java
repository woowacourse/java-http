package org.apache.handler;

import java.io.IOException;
import org.apache.common.ContentType;
import org.apache.common.FileReader;
import org.apache.common.HttpStatus;
import org.apache.request.HttpRequest;
import org.apache.response.HttpResponse;

public class FileHandler implements RequestHandler {

    @Override
    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        String target = httpRequest.getTarget();

        String content = FileReader.read(target);
        return new HttpResponse(HttpStatus.OK, ContentType.from(target), content);
    }
}
