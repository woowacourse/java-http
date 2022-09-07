package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.HttpRequest;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.util.FileUtil;

public class FileHandler {

    public static ResponseEntity handle(HttpRequest httpRequest) {
        String body = FileUtil.readAllBytes(httpRequest.getRequestUri());
        ContentType contentType = ContentType.from(httpRequest.getRequestUri());
        return new ResponseEntity(HttpStatus.OK, body, contentType, new HttpHeaders());
    }
}
