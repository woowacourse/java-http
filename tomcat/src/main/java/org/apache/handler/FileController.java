package org.apache.handler;

import org.apache.common.FileReader;
import org.apache.request.HttpRequest;
import org.apache.response.ContentType;
import org.apache.response.HttpResponse;
import org.apache.response.HttpStatus;

public class FileController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String target = httpRequest.getPath();
        String content = FileReader.read(target);
        httpResponse.setHttpStatus(HttpStatus.OK);
        httpResponse.setContentType(ContentType.from(target));
        httpResponse.setBody(content);
    }
}
