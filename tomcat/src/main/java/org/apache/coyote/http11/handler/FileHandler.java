package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.ResponseEntity;

import java.io.IOException;

public class FileHandler implements Handler {

    public static final String EXTENSION_DELIMITER = ".";

    @Override
    public ResponseEntity handle(HttpRequest request) throws IOException {
        String endPoint = request.getEndPoint();
        int fileTypeStartIndex = endPoint.indexOf(EXTENSION_DELIMITER);
        String fileExtension = endPoint.substring(fileTypeStartIndex + 1);

        String fileData = FileReader.readFile(request.getEndPoint());
        return ResponseEntity.ok(fileData, ContentType.findMatchingType(fileExtension));
    }
}
