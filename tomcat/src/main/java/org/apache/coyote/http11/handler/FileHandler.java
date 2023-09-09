package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FileHandler implements Handler {

    public static final String EXTENSION_DELIMITER = ".";

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String endPoint = httpRequest.getEndPoint();
        int fileTypeStartIndex = endPoint.indexOf(EXTENSION_DELIMITER);
        String fileExtension = endPoint.substring(fileTypeStartIndex + 1);

        String fileData = FileReader.readFile(httpRequest.getEndPoint());
        httpResponse.ok(fileData, ContentType.findMatchingType(fileExtension));
    }
}
