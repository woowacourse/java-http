package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.util.FileUtil;

public class FileHandler {

    public static ResponseEntity handle(HttpRequest httpRequest) {
        String body = FileUtil.readAllBytes(httpRequest.getRequestUri());
        ContentType contentType = ContentType.from(httpRequest.getRequestUri());
        return new ResponseEntity(HttpStatus.OK, body, contentType, new HttpHeaders());
    }
}
