package org.apache.catalina.controller;

import org.apache.catalina.FileReader;
import org.apache.catalina.exception.FileNotReadableException;
import org.apache.coyote.http11.message.ContentType;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class FileController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws FileNotReadableException {
        final String fileContent = FileReader.readStaticFile(httpRequest.getPath());
        httpResponse.setBody(fileContent, ContentType.findResponseContentTypeFromRequest(httpRequest));
        httpResponse.setHttpStatus(HttpStatus.OK);
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        throw new UnsupportedOperationException();
    }
}
