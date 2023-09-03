package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.ResponseEntity;

import java.io.IOException;

public class FileHandler implements Handler {
    @Override
    public ResponseEntity handle(HttpRequest request) throws IOException {
        String fileData = FileReader.readFile(request.getEndPoint());
        return ResponseEntity.ok(fileData, request.getEndPoint());
    }
}
