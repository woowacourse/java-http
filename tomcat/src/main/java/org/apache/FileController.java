package org.apache;

import org.apache.coyote.AbstractController;
import org.apache.coyote.http11.exception.FileNotReadableException;
import org.apache.coyote.http11.message.ContentType;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public class FileController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws FileNotReadableException {
        final String fileContent = fileReader.readStaticFile(httpRequest.getPath());
        httpResponse.setBody(fileContent, ContentType.findResponseContentTypeFromRequest(httpRequest));
        httpResponse.setHttpStatus(HttpStatus.OK);
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws Exception {
        throw new UnsupportedOperationException();
    }
}
